copy c:\erigone\src\erigone.exe
c:\"program files"\"inno setup 5"\iscc.exe eui.iss /feui-%1
move eui-%1.exe dist
pause
