/*
  Satisfiable Tseitin clauses for Kn,n
  Run: erigone -dr -s -lt10000 -nseed sat4-sat.pml
  Search diversity:   Try the verification with different seeds:
  -n91 takes 127389 steps and -n58 takes 361 steps!
*/
active proctype sat() {
	bool p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15;
	bool result = 1;

	if :: p0 = true :: p0 = false fi;
	if :: p1 = true :: p1 = false fi;
	if :: p2 = true :: p2 = false fi;
	if :: p3 = true :: p3 = false fi;
	if :: p4 = true :: p4 = false fi;
	if :: p5 = true :: p5 = false fi;
	if :: p6 = true :: p6 = false fi;
	if :: p7 = true :: p7 = false fi;
	if :: p8 = true :: p8 = false fi;
	if :: p9 = true :: p9 = false fi;
	if :: p10 = true :: p10 = false fi;
	if :: p11 = true :: p11 = false fi;
	if :: p12 = true :: p12 = false fi;
	if :: p13 = true :: p13 = false fi;
	if :: p14 = true :: p14 = false fi;
	if :: p15 = true :: p15 = false fi;

	result = result &&
	(!p0 || !p1 ||  p2 ||  p3) &&
	(!p0 ||  p1 || !p2 ||  p3) &&
	( p0 || !p1 || !p2 ||  p3) &&
	(!p0 ||  p1 ||  p2 || !p3) &&
	( p0 || !p1 ||  p2 || !p3) &&
	( p0 ||  p1 || !p2 || !p3) &&
	(!p0 || !p1 || !p2 || !p3) &&
	( p0 ||  p1 ||  p2 ||  p3) &&
	true;
	result = result &&
	(!p4 ||  p5 ||  p6 ||  p7) &&
	( p4 || !p5 ||  p6 ||  p7) &&
	( p4 ||  p5 || !p6 ||  p7) &&
	(!p4 || !p5 || !p6 ||  p7) &&
	( p4 ||  p5 ||  p6 || !p7) &&
	(!p4 || !p5 ||  p6 || !p7) &&
	(!p4 ||  p5 || !p6 || !p7) &&
	( p4 || !p5 || !p6 || !p7) &&
	true;
	result = result &&
	(!p8 ||  p9 ||  p10 ||  p11) &&
	( p8 || !p9 ||  p10 ||  p11) &&
	( p8 ||  p9 || !p10 ||  p11) &&
	(!p8 || !p9 || !p10 ||  p11) &&
	( p8 ||  p9 ||  p10 || !p11) &&
	(!p8 || !p9 ||  p10 || !p11) &&
	(!p8 ||  p9 || !p10 || !p11) &&
	( p8 || !p9 || !p10 || !p11) &&
	true;
	result = result &&
	(!p12 ||  p13 ||  p14 ||  p15) &&
	( p12 || !p13 ||  p14 ||  p15) &&
	( p12 ||  p13 || !p14 ||  p15) &&
	(!p12 || !p13 || !p14 ||  p15) &&
	( p12 ||  p13 ||  p14 || !p15) &&
	(!p12 || !p13 ||  p14 || !p15) &&
	(!p12 ||  p13 ||  p14 || !p15) &&
	( p12 || !p13 || !p14 || !p15) &&
	true;
	result = result &&
	(!p0 ||  p4 ||  p8 ||  p12) &&
	( p0 || !p4 ||  p8 ||  p12) &&
	( p0 ||  p4 || !p8 ||  p12) &&
	(!p0 || !p4 || !p8 ||  p12) &&
	( p0 ||  p4 ||  p8 || !p12) &&
	(!p0 || !p4 ||  p8 || !p12) &&
	(!p0 ||  p4 || !p8 || !p12) &&
	( p0 || !p4 || !p8 || !p12) &&
	true;
	result = result &&
	(!p1 ||  p5 ||  p9 ||  p13) &&
	( p1 || !p5 ||  p9 ||  p13) &&
	( p1 ||  p5 || !p9 ||  p13) &&
	(!p1 || !p5 || !p9 ||  p13) &&
	( p1 ||  p5 ||  p9 || !p13) &&
	(!p1 || !p5 ||  p9 || !p13) &&
	(!p1 ||  p5 || !p9 || !p13) &&
	( p1 || !p5 || !p9 || !p13) &&
	true;
	result = result &&
	(!p2 ||  p6 ||  p10 ||  p14) &&
	( p2 || !p6 ||  p10 ||  p14) &&
	( p2 ||  p6 || !p10 ||  p14) &&
	(!p2 || !p6 || !p10 ||  p14) &&
	( p2 ||  p6 ||  p10 || !p14) &&
	(!p2 || !p6 ||  p10 || !p14) &&
	(!p2 ||  p6 || !p10 || !p14) &&
	( p2 || !p6 || !p10 || !p14) &&
	true;
	result = result &&
	(!p3 ||  p7 ||  p11 ||  p15) &&
	( p3 || !p7 ||  p11 ||  p15) &&
	( p3 ||  p7 || !p11 ||  p15) &&
	(!p3 || !p7 || !p11 ||  p15) &&
	( p3 ||  p7 ||  p11 || !p15) &&
	(!p3 || !p7 ||  p11 || !p15) &&
	(!p3 ||  p7 || !p11 || !p15) &&
	( p3 || !p7 || !p11 || !p15) &&
	true;

	printf("p0 p1 p2 p3 p4 p5 p6 p7 p8 p9 p10 p11 p12 p13 p14 p15 \n");
	printf(" %d  %d  %d  %d  %d  %d  %d  %d  %d  %d  %d  %d  %d  %d  %d  %d  \n"
	, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15);
	printf("Result = %d\n", result);
	assert(!result);
}
