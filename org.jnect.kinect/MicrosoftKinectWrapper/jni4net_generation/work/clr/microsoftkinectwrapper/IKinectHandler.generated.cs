//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by jni4net. See http://jni4net.sourceforge.net/ 
//     Runtime Version:4.0.30319.261
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace MicrosoftKinectWrapper {
    
    
    #region Component Designer generated code 
    public partial class IKinectHandler_ {
        
        public static global::java.lang.Class _class {
            get {
                return global::MicrosoftKinectWrapper.@__IKinectHandler.staticClass;
            }
        }
    }
    #endregion
    
    #region Component Designer generated code 
    [global::net.sf.jni4net.attributes.JavaProxyAttribute(typeof(global::MicrosoftKinectWrapper.IKinectHandler), typeof(global::MicrosoftKinectWrapper.IKinectHandler_))]
    [global::net.sf.jni4net.attributes.ClrWrapperAttribute(typeof(global::MicrosoftKinectWrapper.IKinectHandler), typeof(global::MicrosoftKinectWrapper.IKinectHandler_))]
    internal sealed partial class @__IKinectHandler : global::java.lang.Object, global::MicrosoftKinectWrapper.IKinectHandler {
        
        internal new static global::java.lang.Class staticClass;
        
        internal static global::net.sf.jni4net.jni.MethodId j4n_setUpAndRun0;
        
        internal static global::net.sf.jni4net.jni.MethodId j4n_testSkeletonTracking1;
        
        internal static global::net.sf.jni4net.jni.MethodId j4n_getSkeleton2;
        
        private @__IKinectHandler(global::net.sf.jni4net.jni.JNIEnv @__env) : 
                base(@__env) {
        }
        
        private static void InitJNI(global::net.sf.jni4net.jni.JNIEnv @__env, java.lang.Class @__class) {
            global::MicrosoftKinectWrapper.@__IKinectHandler.staticClass = @__class;
            global::MicrosoftKinectWrapper.@__IKinectHandler.j4n_setUpAndRun0 = @__env.GetMethodID(global::MicrosoftKinectWrapper.@__IKinectHandler.staticClass, "setUpAndRun", "()Ljava/lang/String;");
            global::MicrosoftKinectWrapper.@__IKinectHandler.j4n_testSkeletonTracking1 = @__env.GetMethodID(global::MicrosoftKinectWrapper.@__IKinectHandler.staticClass, "testSkeletonTracking", "()Ljava/lang/String;");
            global::MicrosoftKinectWrapper.@__IKinectHandler.j4n_getSkeleton2 = @__env.GetMethodID(global::MicrosoftKinectWrapper.@__IKinectHandler.staticClass, "getSkeleton", "()Ljava/lang/String;");
        }
        
        public string setUpAndRun() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            using(new global::net.sf.jni4net.jni.LocalFrame(@__env, 10)){
            return global::net.sf.jni4net.utils.Convertor.StrongJ2CString(@__env, @__env.CallObjectMethodPtr(this, global::MicrosoftKinectWrapper.@__IKinectHandler.j4n_setUpAndRun0));
            }
        }
        
        public string testSkeletonTracking() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            using(new global::net.sf.jni4net.jni.LocalFrame(@__env, 10)){
            return global::net.sf.jni4net.utils.Convertor.StrongJ2CString(@__env, @__env.CallObjectMethodPtr(this, global::MicrosoftKinectWrapper.@__IKinectHandler.j4n_testSkeletonTracking1));
            }
        }
        
        public string getSkeleton() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            using(new global::net.sf.jni4net.jni.LocalFrame(@__env, 10)){
            return global::net.sf.jni4net.utils.Convertor.StrongJ2CString(@__env, @__env.CallObjectMethodPtr(this, global::MicrosoftKinectWrapper.@__IKinectHandler.j4n_getSkeleton2));
            }
        }
        
        private static global::System.Collections.Generic.List<global::net.sf.jni4net.jni.JNINativeMethod> @__Init(global::net.sf.jni4net.jni.JNIEnv @__env, global::java.lang.Class @__class) {
            global::System.Type @__type = typeof(__IKinectHandler);
            global::System.Collections.Generic.List<global::net.sf.jni4net.jni.JNINativeMethod> methods = new global::System.Collections.Generic.List<global::net.sf.jni4net.jni.JNINativeMethod>();
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "setUpAndRun", "setUpAndRun0", "()Ljava/lang/String;"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "testSkeletonTracking", "testSkeletonTracking1", "()Ljava/lang/String;"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "getSkeleton", "getSkeleton2", "()Ljava/lang/String;"));
            return methods;
        }
        
        private static global::net.sf.jni4net.utils.JniHandle setUpAndRun0(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj) {
            // ()Ljava/lang/String;
            // ()LSystem/String;
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            global::net.sf.jni4net.utils.JniHandle @__return = default(global::net.sf.jni4net.utils.JniHandle);
            try {
            global::MicrosoftKinectWrapper.IKinectHandler @__real = global::net.sf.jni4net.utils.Convertor.FullJ2C<global::MicrosoftKinectWrapper.IKinectHandler>(@__env, @__obj);
            @__return = global::net.sf.jni4net.utils.Convertor.StrongC2JString(@__env, @__real.setUpAndRun());
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return @__return;
        }
        
        private static global::net.sf.jni4net.utils.JniHandle testSkeletonTracking1(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj) {
            // ()Ljava/lang/String;
            // ()LSystem/String;
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            global::net.sf.jni4net.utils.JniHandle @__return = default(global::net.sf.jni4net.utils.JniHandle);
            try {
            global::MicrosoftKinectWrapper.IKinectHandler @__real = global::net.sf.jni4net.utils.Convertor.FullJ2C<global::MicrosoftKinectWrapper.IKinectHandler>(@__env, @__obj);
            @__return = global::net.sf.jni4net.utils.Convertor.StrongC2JString(@__env, @__real.testSkeletonTracking());
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return @__return;
        }
        
        private static global::net.sf.jni4net.utils.JniHandle getSkeleton2(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj) {
            // ()Ljava/lang/String;
            // ()LSystem/String;
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            global::net.sf.jni4net.utils.JniHandle @__return = default(global::net.sf.jni4net.utils.JniHandle);
            try {
            global::MicrosoftKinectWrapper.IKinectHandler @__real = global::net.sf.jni4net.utils.Convertor.FullJ2C<global::MicrosoftKinectWrapper.IKinectHandler>(@__env, @__obj);
            @__return = global::net.sf.jni4net.utils.Convertor.StrongC2JString(@__env, @__real.getSkeleton());
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return @__return;
        }
        
        new internal sealed class ContructionHelper : global::net.sf.jni4net.utils.IConstructionHelper {
            
            public global::net.sf.jni4net.jni.IJvmProxy CreateProxy(global::net.sf.jni4net.jni.JNIEnv @__env) {
                return new global::MicrosoftKinectWrapper.@__IKinectHandler(@__env);
            }
        }
    }
    #endregion
}