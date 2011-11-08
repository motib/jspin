cd docs
pdflatex -quiet eui-user
pdflatex -quiet eui-user
pdflatex -quiet eui-quick
pdflatex -quiet eui-quick
copy *.pdf c:\web\erigone\
cd ..
javac -target 1.5 eui\*.java
jar cfm EUI.jar eui\MANIFEST.MF eui\*.class
pause
