/* Fourth attempt */
bit     wantp = 0, wantq = 0;
#define live (p@csp)

active proctype p() {
    do :: wantp = 1;
        do  :: wantq == 0 -> break;
            :: wantq == 1 ->
                   wantp = 0;
                   wantp = 1
        od;
csp:	wantp = 0; 
    od
}

active proctype q() {
    do :: wantq = 1;
        do  :: wantp == 0 -> break;
            :: wantp == 1 ->
                    wantq = 0;
                    wantq = 1
        od;
		wantq = 0; 
    od
}

