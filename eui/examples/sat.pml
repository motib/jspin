active proctype sat() {
	bool a, b, c, d, e, f, g, h, i;
	bool result;

	if :: a = true :: a = false fi;
	if :: b = true :: b = false fi;
	if :: c = true :: c = false fi;
	if :: d = true :: d = false fi;
	if :: e = true :: e = false fi;
	if :: f = true :: f = false fi;
	if :: g = true :: g = false fi;
	if :: h = true :: h = false fi;
	if :: i = true :: i = false fi;

	result =
	(!a || !b ||  c) &&
	(!a ||  b || !c) &&
	( a || !b || !c) &&
	( a ||  b ||  c) &&
	(!d ||  e ||  f) &&
	( d || !e ||  f) &&
	( d ||  e || !f) &&
	(!d || !e || !f);
  result = result &&
	(!g ||  h ||  i) &&
	( g || !h ||  i) &&
	( g ||  h || !i) &&
	(!g || !h || !i) &&
	(!a ||  d ||  g) &&
	( a || !d ||  g) &&
	( a ||  d || !g) &&
	(!a || !d || !g);
  result = result &&
	(!b ||  e ||  h) &&
	( b || !e ||  h) &&
	( b ||  e || !h) &&
	(!b || !e || !h) &&
	(!c ||  f ||  i) &&
	( c || !f ||  i) &&
	( c ||  f || !i) &&
	(!c || !f || !i);

	printf("a b c d e f g h i \n");
	printf("%d %d %d %d %d %d %d %d %d \n",a,b,c,d,e,f,g,h,i);
	printf("Result = %d\n", result);

	assert(!result)
}
