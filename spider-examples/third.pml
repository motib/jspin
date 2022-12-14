bool wantp = false, wantq = false;

active proctype p() {
    do :: wantp = true; 
          !wantq;
cs:       wantp = false;
    od
}

active proctype q() {
    do :: 
wantq = true;
          !wantp;
cs:       wantq = false;
    od
}

