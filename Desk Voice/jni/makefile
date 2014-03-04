# Define a variable for classpath
CLASS_PATH = ../bin

# Define a virtual path for .class in the bin directory
vpath %.class $(CLASS_PATH)

all : deskvoice.dll

# $@ matches the target, $< matches the first dependancy
deskvoice.dll : CommandControl.o
	gcc -Wl,--add-stdcall-alias -shared -o $@ $<

# $@ matches the target, $< matches the first dependancy
CommandControl.o : com_arianjm_tfg_deskvoice_CommandControl.c com_arianjm_tfg_deskvoice_CommandControl.h
	gcc -I"C:\Program Files\Java\jdk1.7.0_21\include" -I"C:\Program Files\Java\jdk1.7.0_21\include\win32" -c $< -o $@

# $* matches the target filename without the extension
#CommandControl.h : CommandControl.class
#	javah -classpath $(CLASS_PATH) $*

clean :
	rm CommandControl.o deskvoice.dll