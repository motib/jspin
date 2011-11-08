byte sem = 1;
byte critical = 0;

active proctype p() {	
  do :: 
    atomic {
      sem > 0;
      sem--
    }
    critical++;
    assert(critical == 1);
    critical--;
    sem++
  od
}

active proctype q() {	
  do :: 
    atomic {
      sem > 0;
      sem--
    }
    critical++;
    assert(critical == 1);
    critical--;
    sem++
  od
}

active proctype r() {	
  do :: 
    atomic {
      sem > 0;
      sem--
    }
    critical++;
    assert(critical == 1);
    critical--;
    sem++
  od
}
