JARFILE=infect.jar
BINDIR=bin
SRCDIR=src

echo compile...
mkdir -p $BINDIR
javac -d $BINDIR -source 1.8 -target 1.8 -Xlint $SRCDIR/infect/g2Ddemo/*.java
cp -rp $SRCDIR/resource $BINDIR

echo build jar...
rm -f $JARFILE
jar -c -e infect.g2Ddemo.Launcher -f $JARFILE -C bin/ .
chmod u+x $JARFILE

echo done.
echo to run it, use:  java -jar $JARFILE

