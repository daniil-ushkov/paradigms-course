% :NOTE: it can be written shortly, but ok

% Sieve of Eratosthenes
assert_composites(CUR, _, MAX_N) :- CUR > MAX_N, !.

assert_composites(CUR, STEP, MAX_N) :- 
	assert(composite(CUR)), 
	NEXT is CUR + STEP, 
	assert_composites(NEXT, STEP, MAX_N).


find_composites(CUR, MAX_N) :- CUR * CUR > MAX_N, !.

find_composites(CUR, MAX_N) :- 
	composite(CUR), 
	NEXT is CUR + 1, 
	find_composites(NEXT, MAX_N).
	
find_composites(CUR, MAX_N) :- 
	\+ composite(CUR), 
	CUR1 is CUR * CUR, 
	assert_composites(CUR1, CUR, MAX_N), 
	NEXT is CUR + 1, 
	find_composites(NEXT, MAX_N).


init(MAX_N) :- find_composites(2, MAX_N).


prime(N) :- N > 1, \+ composite(N).

divide(N, D) :- 0 is mod(N, D).

% number to factorization
find_prime_divisors(N, CUR, [N]) :- N < CUR * CUR, !.

find_prime_divisors(N, CUR, [CUR | DIVS_T]) :- 
	divide(N, CUR), 
	N1 is div(N, CUR), 
	find_prime_divisors(N1, CUR, DIVS_T).
	
find_prime_divisors(N, CUR, DIVS) :- 
	\+ divide(N, CUR), 
	NEXT is CUR + 1, 
	find_prime_divisors(N, NEXT, DIVS).
	

prime_divisors(N, DIVS_R) :- 
	integer(N), 
	N > 1, 
	find_prime_divisors(N, 2, DIVS_R).

% factrization to number
in_order(H, []).

in_order(H, [H1 | T]) :- H =< H1.


prime_divisors(1, []).

prime_divisors(R, [H | T]) :- 
	integer(H),
  prime(H), 
	in_order(H, T), 
	prime_divisors(R1, T), 
	R is H * R1.

% 38 modification
list_radix(0, RAD, []) :- !.

list_radix(N, RAD, [H | T]) :- 
	H is mod(N, RAD), 
	N1 is div(N, RAD), 
	list_radix(N1, RAD, T). 

equals([], []).

equals([H1 | T1], [H2 | T2]) :-
	H1 is H2,
	equals(T1, T2).

prime_palindrome(N, RAD) :- 
	prime(N), 
	list_radix(N, RAD, L), 
	reverse(L, LR), 
	equals(L, LR).