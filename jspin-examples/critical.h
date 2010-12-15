/* Copyright (C) 2006 M. Ben-Ari. See copyright.txt */
/* 
  Definitions for critical section
  If K is defined, checks (critical <= K), otherwise (critical == 1).
  If PID is defined, prints _pid in CS, otherwise prints a character parameter.
  If NOSTARVE is defined, you can Verify Acceptance of <>nostarve.
*/

#ifdef NOSTARVE
bool P1inCS = false;
#define nostarve P1inCS
#endif

byte critical = 0; 

#ifdef PID
inline critical_section() {
     printf("MSC: %d in CS\n", _pid);
#else
inline critical_section(proc) {
     printf("MSC: %c in CS\n", proc);
#endif
     critical++;
#ifdef K
     assert (critical <= K);
#else
     assert (critical == 1);
#endif
#ifdef NOSTARVE
	   if :: _pid == 1 -> 
                P1inCS = true; P1inCS = false
          :: else
       fi;
#endif
	   critical--;
}

