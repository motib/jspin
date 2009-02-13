/* Fourth attempt */
bool wantp = false, wantq = false;
bool csp = false, csq = false;

active proctype p() {
    do 
    :: wantp = true;
       do
       :: !wantq -> break;
       :: wantq ->
            wantp = false;
            wantp = true
       od;
       csp = true;
       assert (!(csp && csq));
       csp = false;
	   wantp = false; 
    od
}

active proctype q() {
    do 
    :: wantq = true;
       do
       :: !wantp -> break;
       :: wantp ->
            wantq = false;
            wantq = true
       od;
       csq = true;
       assert (!(csp && csq));
       csq = false;
	   wantq = false; 
    od
}
