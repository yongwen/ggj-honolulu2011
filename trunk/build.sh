cd bin
jar cvfm ../lib/roach.jar ../MANIFEST.MF *
cd ..
cp lib/*.jar release
cp lib/*.html release
jar cvfM roachgame.zip license.txt release src press
