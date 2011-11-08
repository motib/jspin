byte n = 0;
bool flag = false;

ltl { <>flag }

active proctype p() {
	do
	::  flag -> break;
	::  else -> n = 1 - n;
	od
}

active proctype q() {
	flag = true
}
