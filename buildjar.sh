JARFILE=infect.jar

rm -f $JARFILE
jar -c -e infect.g2Ddemo.Launcher -f $JARFILE -C bin/ .
chmod u+x $JARFILE
