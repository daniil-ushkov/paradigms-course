% :NOTE: it can be written shortly, but ok

map_build([], []).
map_build([(K, V) | T], RES) :- 
	map_build(T, RES_T), 
	map_put(RES_T, K, V, RES).

merge(TREE_L, [], TREE_L).
merge([], TREE_R, TREE_R).
merge(TREE_L, TREE_R, RES) :- 
	TREE_L = node(KL, VL, PRIOR_L, TREE_LL, TREE_LR), 
	TREE_R = node(KR, VR, PRIOR_R, TREE_RL, TREE_RR),
	((PRIOR_L > PRIOR_R, merge(TREE_LR, TREE_R, MERGED), RES = node(KL, VL, PRIOR_L, TREE_LL, MERGED));
	(PRIOR_L =< PRIOR_R, merge(TREE_L, TREE_RL, MERGED), RES = node(KR, VR, PRIOR_R, MERGED, TREE_RR))).

split([], _, [], []).
split(TREE, SEP, RES_L, RES_R) :- 
	TREE = node(K, V, PRIOR, L, R),
	((SEP < K, split(L, SEP, PART_L, PART_R), RES_L = PART_L, RES_R = node(K, V, PRIOR, PART_R, R));
	(SEP >= K, split(R, SEP, PART_L, PART_R), RES_L = node(K, V, PRIOR, L, PART_L), RES_R = PART_R)).

insert(TREE, NODE, RES) :-
	NODE = node(K, _, _, _, _),
	K1 is K - 1,
	split(TREE, K1, L, REST), 
	split(REST, K, M, R),
	merge(L, NODE, FST),
	merge(FST, R, RES).


map_put(TREE, K, V, R) :-
	rand_int(1000, PRIOR),
	NEW = node(K, V, PRIOR, [], []),
	insert(TREE, NEW, R).

map_get(node(K, V, _, _, _), K, V).
map_get(node(K, V, _, L, R), FIND_K, FIND_V) :- 
	(FIND_K < K, map_get(L, FIND_K, FIND_V));
	(FIND_K > K, map_get(R, FIND_K, FIND_V)).

map_remove(TREE, K, RES) :-
	K1 is K - 1,
	split(TREE, K1, L, REST), 
	split(REST, K, M, R),
	merge(L, R, RES).


% 38 modification

min(TREE, RES) :-
	TREE = node(K, _, _, L, _),
	((L \= [], min(L, RES)); (L == [], RES is K)).

map_ceilingKey(TREE, K, CEIL_KEY) :-
	K1 is K - 1,
	split(TREE, K1, L, R), 
	min(R, CEIL_KEY).