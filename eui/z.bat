set back=eui
7z u -r -x!*~ -tzip c:\%back%\%back%.zip *.java *.mf *.pml *.prp *.tex *.ico *.iss *.html *.txt *.png *.eps
copy %back%.zip \zip
copy %back%.zip n:\zip
set back=
pause

