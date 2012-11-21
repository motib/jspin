cd docs
pdflatex -quiet eui-user
pdflatex -quiet eui-user
pdflatex -quiet eui-quick
pdflatex -quiet eui-quick
cd ..
javac eui\*.java
jar cfm EUI.jar eui\MANIFEST.MF eui\*.class
pause
