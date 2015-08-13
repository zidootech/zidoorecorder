# zidoorecorder

This repository is the source code of hdmi recorder for Zidoo X9.

You can build the apk by Eclipse. Yes, we have prebuild the jni.  
You may build jni yourself if you have a copy of Mstar Kikat source code.

If you build apk by Eclipse, you must signed the apk with system key. 
If not, the apk can not record.

Here is the method to sign system key:

unpack mstar_system_sign_tool.tar.gz on linux terminal.
$tar zxf mstar_system_sign_tool.tar.gz 
$cd mstar_system_sign_tool

then copy the apk to this path. execute command:
$java -jar signapk.jar platform.x509.pem platform.pk8  your.apk your_signed.apk

your.apk means the apk you build by Eclipse.
your_signed.apk is the output apk with system signed.

Enjoy yourself.
