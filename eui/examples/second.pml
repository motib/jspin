bool wantp = false, wantq = false;
byte critical = 0; 

active proctype p() {
    do 
    ::
       !wantq;
       wantp = true;
       critical++;
       assert (critical == 1);
       critical--;
       wantp = false;
    od
}

active proctype q() {
    do 
    ::
       !wantp;
       wantq = true;
       critical++;
       assert (critical == 1);
       critical--;
       wantq = false;
    od
}
