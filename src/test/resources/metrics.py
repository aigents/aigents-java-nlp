fname = "results.txt"

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
import numpy as np
def wer_score(hyp, ref, print_matrix=False):
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
        # print("{} - {}: del {} ins {} sub {} s {}".format(hyp[i], ref[j], deletion, insertion, substitution, sub))
  if print_matrix:
    print("WER matrix ({}x{}): ".format(N, M))
    print(L)
  return int(L[N-1, M-1])

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
