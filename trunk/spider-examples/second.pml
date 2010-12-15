/* Second attempt */
bool wantp = false, wantq = false;

active proctype p() {
    do ::
 !wantq;
          wantp = true;
csp:      wantp = false;
    od
}

active proctype q() {
    do :: !wantp;
          wantq = true;
csq:      wantq = false;
    od
}

ltl { !<>(p@csp && q@csq) }
