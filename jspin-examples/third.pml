/* Third attempt */
bool wantp = false, wantq = false;
byte critical = 0; 

active proctype p() {
    do 
	::
 wantp = true;
	   !wantq;
       critical++;
       assert (critical == 1);
       critical--;
       wantp = false;
    od
}

active proctype q() {
    do 
    ::
 wantq = true;
	   !wantp;
       critical++;
       assert (critical == 1);
       critical--;
       wantq = false;
    od
}

