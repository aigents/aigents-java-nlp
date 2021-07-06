import json

data = {}
data['version'] = "v2.0"
data['data'] = []

d = {}
d['title'] = "POC_English"
d['paragraphs'] = []

context1 = {}
context2 = {}
context3 = {}

rel_and_food = "A mom is a human. A dad is a human. A mom is a parent. A dad is a parent. A son is a child. A daughter is a child. A son is a human. A daughter is a human. A mom likes cake. A daughter likes cake. A son likes sausage. A dad likes sausage. Cake is a food. Sausage is a food. Mom is a human now. Dad is a human now. Mom is a parent now. Dad is a parent now. Son is a child now. Daughter is a child now. Son is a human now. Daughter is a human now. Mom likes cake now. Daughter likes cake now. Son likes sausage now. Dad likes sausage now. Cake is a food now. Sausage is a food now. Mom was a daughter before. Dad was a son before. Mom was not a parent before. Dad was not a parent before. Mom liked cake before. Dad liked sausage before. Cake was a food before. Sausage was a food before."
prof = "Mom is on the board of directors. Dad is on the board of directors. Son is on the board of directors. Daughter is on the board of directors. Mom writes with chalk on the board. Dad writes with chalk on the board. Son writes with chalk on the board. Daughter writes with chalk on the board. Dad wants Mom to be on the board of directors. Mom wants Dad to be on the board of directors. Dad wants his son to be on the board of directors. Mom wants her daughter to be on the board of directors. Mom writes to Dad with chalk on the board. Dad writes to Mom with chalk on the board. Son writes to Dad with chalk on the board. Daughter writes to Mom with chalk on the board."
tools_and_pos = "Mom has a hammer. Mom has a saw. Dad has a hammer. Dad has a saw. Mom has a telescope. Mom has binoculars. Dad has a telescope. Dad has binoculars. Mom saw Dad with a hammer. Mom saw Dad with a saw. Dad saw Mom with a hammer. Dad saw Mom with a saw. Saw is a tool. Hammer is a tool. Binoculars are a tool. A telescope is a tool. Mom sawed the wood with a saw. Dad sawed the wood with a saw. Son sawed the wood with a saw. Daughter sawed the wood with a saw. Mom knocked the wood with a hammer. Dad knocked the wood with a hammer. Son knocked the wood with a hammer. Daughter knocked the wood with a hammer. Mom saw Dad with binoculars. Mom saw Dad with a telescope. Dad saw Mom with binoculars. Dad saw Mom with a telescope."

context1['context'] = rel_and_food
context2['context'] = tools_and_pos
context3['context'] = prof

context1['qas'] = []
context2['qas'] = []
context3['qas'] = []

answers = []

pc = open("poc_english.txt", "r")
for line in pc:
    answers.append(line.rstrip() + ".")


f = open("poc_english_queries.txt", "r")
i = 0
    
for line in f:
    parts = line.split(" ")
    question = ""
    for k in range(len(parts)-1):
        question = question + parts[k+1].rstrip() + " "
    question = question[0:len(question)-1]
    answer = answers[i]
    if "relationships" in parts[0]:
        context1['qas'].append({
            "question": question,
            "id": str(i),
            "answers": [
                { "text": answer, "answer_start": rel_and_food.index(answer) }
            ],
            "is_impossible": False
        })
    elif "tools" in parts[0]:
        context2['qas'].append({
            "question": question,
            "id": str(i),
            "answers": [
                { "text": answer, "answer_start": tools_and_pos.index(answer) }
            ],
            "is_impossible": False
        })
    else:
        context3['qas'].append({
            "question": question,
            "id": str(i),
            "answers": [
                { "text": answer, "answer_start": prof.index(answer) }
            ],
            "is_impossible": False
        })
    i += 1

d['paragraphs'].append(context1)
d['paragraphs'].append(context2)
d['paragraphs'].append(context3)

data['data'].append(d)

with open('train-v2.0.json', 'w') as outfile:
    json.dump(data, outfile)

with open('dev-v2.0.json', 'w') as outfile:
    json.dump(data, outfile)