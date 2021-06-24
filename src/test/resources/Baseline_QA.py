from transformers import AutoModelForQuestionAnswering, AutoTokenizer

mnames = ['deepset/bert-base-cased-squad2', 'deepset/electra-base-squad2', 'twmkn9/albert-base-v2-squad2', 'deepset/roberta-base-squad2', 'Primer/bart-squad2']

def wer_score(hyp, ref, print_matrix=False):
    import numpy as np
    N = len(hyp)
    M = len(ref)
    L = np.zeros((N,M))
    for i in range(0, N):
        for j in range(0, M):
            if min(i,j) == 0:
                L[i,j] = max(i,j)
            else:
                deletion = L[i-1,j] + 1
                insertion = L[i,j-1] + 1
                sub = 1 if hyp[i] != ref[j] else 0
                substitution = L[i-1,j-1] + sub
                L[i,j] = min(deletion, min(insertion, substitution))
    if print_matrix:
        print("WER matrix ({}x{}): ".format(N, M))
        print(L)
    return int(L[N-1, M-1])

def metrics(fname):
    # BLEU
    from nltk.translate.bleu_score import sentence_bleu, corpus_bleu
    scores = []
    f = open("poc_english.txt", "r")
    f2 = open(fname, "r")
    lines = f.readlines()
    cand = f2.readlines()
    for i in range(len(cand)):
        line = lines[i]
        candidate = []
        l = cand[i].lower().strip('\n')[1:len(cand[i])-2].split(", ")
        for item in l:
            item = item.strip('.').split(" ")
            candidate.append(item)
        arr = line.strip('.\n').split(" ")
        for i in range(len(arr)):
            arr[i] = arr[i].lower()
        reference = [arr]
        for c in candidate:
            # print(reference, c, ': ', sentence_bleu(reference, c, weights=(1,0)))
            scores.append(sentence_bleu(reference, c, weights=(1,0)))

    print("BLEU: " + str(sum(scores)/(1.0*len(scores))))

    # Word2Vec Cosine Similarity
    import torch
    import torch.nn.functional as F
    from sentence_transformers import SentenceTransformer
    import nltk
    from nltk import tokenize
    def similarity(par1, par2):
        transformer = SentenceTransformer('roberta-base-nli-stsb-mean-tokens')
        transformer.eval()
        par1 = tokenize.sent_tokenize(par1)
        vec1 = torch.Tensor(transformer.encode(par1))
        vec1 = vec1.mean(0)
        par2 = tokenize.sent_tokenize(par2)
        vec2 = torch.Tensor(transformer.encode(par2))
        vec2 = vec2.mean(0)
        cos_sim = F.cosine_similarity(vec1, vec2, dim=0)
        return cos_sim.item()

    scores = []
    f = open("poc_english.txt", "r")
    f2 = open(fname, "r")
    lines = f.readlines()
    cand = f2.readlines()
    for i in range(len(cand)):
        line = lines[i]
        candidate = []
        l = cand[i].lower().strip('\n')[1:len(cand[i])-2].split(", ")
        for item in l:
            item = item.strip('.').split(" ")
            candidate.append(item)
        arr = line.strip('.\n').split(" ")
        if (len(arr) == 1):
            continue
        for i in range(len(arr)):
            arr[i] = arr[i].lower()
        reference = arr
        for c in candidate:
            scores.append(similarity(" ".join(reference), " ".join(c)))
    print("Word2Vec Cosine Similarity: " + str(sum(scores)/(1.0*len(scores))))

    # WER
    scores = []
    f = open("poc_english.txt", "r")
    f2 = open(fname, "r")
    lines = f.readlines()
    cand = f2.readlines()
    for i in range(len(cand)):
        line = lines[i]
        candidate = []
        l = cand[i].lower().strip('\n')[1:len(cand[i])-2].split(", ")
        for item in l:
            item = item.strip('.').split(" ")
            candidate.append(item)
        arr = line.strip('.\n').split(" ")
        if (len(arr) == 1):
            continue
        for i in range(len(arr)):
            arr[i] = arr[i].lower()
        reference = arr
        for c in candidate:
            scores.append(wer_score(c, reference))
    print("WER: " + str(sum(scores)/(1.0*len(scores))))

    # TER
    import pyter

    scores = []
    f = open("poc_english.txt", "r")
    f2 = open(fname, "r")
    lines = f.readlines()
    cand = f2.readlines()
    for i in range(len(cand)):
        line = lines[i]
        candidate = []
        l = cand[i].lower().strip('\n')[1:len(cand[i])-2].split(", ")
        for item in l:
            item = item.strip('.').split(" ")
            candidate.append(item)
        arr = line.strip('.\n').split(" ")
        if (len(arr) == 1):
            continue
        for i in range(len(arr)):
            arr[i] = arr[i].lower()
        reference = arr
        for c in candidate:
            scores.append(pyter.ter(reference, c))
    print("TER: " + str(sum(scores)/(1.0*len(scores))))

def run(modelname):
    model = AutoModelForQuestionAnswering.from_pretrained(modelname)
    tokenizer = AutoTokenizer.from_pretrained(modelname)

    from transformers import pipeline
    nlp = pipeline('question-answering', model=model, tokenizer=tokenizer)

    rel_and_food = "A mom is a human. A dad is a human. A mom is a parent. A dad is a parent. A son is a child. A daughter is a child. A son is a human. A daughter is a human. A mom likes cake. A daughter likes cake. A son likes sausage. A dad likes sausage. Cake is a food. Sausage is a food. Mom is a human now. Dad is a human now. Mom is a parent now. Dad is a parent now. Son is a child now. Daughter is a child now. Son is a human now. Daughter is a human now. Mom likes cake now. Daughter likes cake now. Son likes sausage now. Dad likes sausage now. Cake is a food now. Sausage is a food now. Mom was a daughter before. Dad was a son before. Mom was not a parent before. Dad was not a parent before. Mom liked cake before. Dad liked sausage before. Cake was a food before. Sausage was a food before."
    prof = "Mom is on the board of directors. Dad is on the board of directors. Son is on the board of directors. Daughter is on the board of directors. Mom writes with chalk on the board. Dad writes with chalk on the board. Son writes with chalk on the board. Daughter writes with chalk on the board. Dad wants Mom to be on the board of directors. Mom wants Dad to be on the board of directors. Dad wants his son to be on the board of directors. Mom wants her daughter to be on the board of directors. Mom writes to Dad with chalk on the board. Dad writes to Mom with chalk on the board. Son writes to Dad with chalk on the board. Daughter writes to Mom with chalk on the board."
    tools_and_pos = "Mom has a hammer. Mom has a saw. Dad has a hammer. Dad has a saw. Mom has a telescope. Mom has binoculars. Dad has a telescope. Dad has binoculars. Mom saw Dad with a hammer. Mom saw Dad with a saw. Dad saw Mom with a hammer. Dad saw Mom with a saw. Saw is a tool. Hammer is a tool. Binoculars are a tool. A telescope is a tool. Mom sawed the wood with a saw. Dad sawed the wood with a saw. Son sawed the wood with a saw. Daughter sawed the wood with a saw. Mom knocked the wood with a hammer. Dad knocked the wood with a hammer. Son knocked the wood with a hammer. Daughter knocked the wood with a hammer. Mom saw Dad with binoculars. Mom saw Dad with a telescope. Dad saw Mom with binoculars. Dad saw Mom with a telescope."

    f = open("poc_english_queries.txt", "r")
    f2name = modelname.split("/")[1] + ".txt"
    f2 = open(f2name, "w")
    
    for line in f:
        parts = line.split(" ")
        context = ""
        if "relationships" in parts[0]:
            context = rel_and_food
        elif "tools" in parts[0]:
            context = tools_and_pos
        else:
            context = prof
        question = ""
        for i in range(len(parts)-1):
            question = question + parts[i+1].rstrip() + " "
        question = question[0:len(question)-1] + "?"
        f2.write(nlp({'question': question, 'context': context })['answer'].replace(".",",") + "\n")
    
    f2.close()

    print(f2name)
    metrics(f2name)
    print('\n')

for m in mnames:
    run(m)