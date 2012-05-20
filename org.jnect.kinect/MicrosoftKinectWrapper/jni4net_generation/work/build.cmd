@echo off
if not exist target mkdir target
if not exist target\classes mkdir target\classes


echo compile classes
javac -nowarn -d target\classes -sourcepath jvm -cp "d:\workspace_jnect\jnectemf\org.jnect.kinect\microsoftkinectwrapper\jni4net_generation\jni4net.j-0.8.6.0.jar"; "jvm\microsoftkinectwrapper\IKinectHandler.java" "jvm\microsoftkinectwrapper\IKinectHandler_.java" "jvm\microsoftkinectwrapper\ISpeechRecognition.java" "jvm\microsoftkinectwrapper\ISpeechRecognition_.java" "jvm\microsoftkinectwrapper\KinectHandler.java" "jvm\microsoftkinectwrapper\SpeechRecognition.java" 
IF %ERRORLEVEL% NEQ 0 goto end


echo MicrosoftKinectWrapper.j4n.jar 
jar cvf MicrosoftKinectWrapper.j4n.jar  -C target\classes "microsoftkinectwrapper\IKinectHandler.class"  -C target\classes "microsoftkinectwrapper\IKinectHandler_.class"  -C target\classes "microsoftkinectwrapper\__IKinectHandler.class"  -C target\classes "microsoftkinectwrapper\ISpeechRecognition.class"  -C target\classes "microsoftkinectwrapper\ISpeechRecognition_.class"  -C target\classes "microsoftkinectwrapper\__ISpeechRecognition.class"  -C target\classes "microsoftkinectwrapper\KinectHandler.class"  -C target\classes "microsoftkinectwrapper\SpeechRecognition.class"  > nul 
IF %ERRORLEVEL% NEQ 0 goto end


echo MicrosoftKinectWrapper.j4n.dll 
csc /nologo /warn:0 /t:library /out:MicrosoftKinectWrapper.j4n.dll /recurse:clr\*.cs  /reference:"D:\workspace_jnect\jnectemf\org.jnect.kinect\MicrosoftKinectWrapper\jni4net_generation\work\MicrosoftKinectWrapper.dll" /reference:"D:\workspace_jnect\jnectemf\org.jnect.kinect\MicrosoftKinectWrapper\jni4net_generation\jni4net.n-0.8.6.0.dll"
IF %ERRORLEVEL% NEQ 0 goto end


:end
