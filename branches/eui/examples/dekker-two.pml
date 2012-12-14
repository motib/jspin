bool    wantp = false, wantq = false;
byte    turn = 1;
bool csp = false, csq = false;

ltl mutex    { []!(csp && csq) }
ltl nostarve { []<>csp && []<>csq }

active proctype p() {
    do
    ::  wantp = true;
        do
        :: !wantq -> break;
        :: else ->
            if
            :: (turn == 1)
            :: (turn == 2) ->
                wantp = false;
                (turn == 1);
                wantp = true
            fi
        od;
        csp = true;
        csp = false;
        wantp = false;
        turn = 2
    od
}

active proctype q() {
    do
    ::  wantq = true;
        do
        :: !wantp -> break;
        :: else ->
            if
            :: (turn == 2)
            :: (turn == 1) ->
                wantq = false;
                (turn == 2);
                wantq = true
            fi
        od;
        csq = true;
        csq = false;
        wantq = false;
        turn = 1
    od
}
