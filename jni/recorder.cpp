#define LOG_NDEBUG 0
#define LOG_TAG "hdmiRecorder"
#include <utils/Log.h>
#include <stdio.h>
#include "jni.h"
#include "JNIHelp.h"
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <linux/input.h>
#include <sys/ioctl.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <camera/Camera.h>
#include <camera/ICamera.h>
#include <media/mediarecorder.h>
#include <utils/Log.h>
#include <time.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <utils/Log.h>
#include <utils/String8.h>
#include <binder/IPCThreadState.h>
#include <binder/ProcessState.h>
#include <binder/IServiceManager.h>
#include "MsTypes.h"
#include "DApi_Capture.h"
#include "android_runtime/AndroidRuntime.h"

#define LOG_TAG "hdmi_jni"

using namespace android;

struct fields_t {
	jfieldID context;
	jfieldID surface;

	jmethodID post_event;
};
static fields_t fields;
static Mutex sLock;

// ----------------------------------------------------------------------------
// ref-counted object for callbacks
class JNIMediaRecorderListener: public MediaRecorderListener {
public:
	JNIMediaRecorderListener(JNIEnv* env, jobject thiz, jobject weak_thiz);
	~JNIMediaRecorderListener();
	void notify(int msg, int ext1, int ext2);
private:
	JNIMediaRecorderListener();
	jclass mClass; // Reference to MediaRecorder class
	jobject mObject; // Weak ref to MediaRecorder Java object to call on
};

JNIMediaRecorderListener::JNIMediaRecorderListener(JNIEnv* env, jobject thiz,
		jobject weak_thiz) {

	// Hold onto the MediaRecorder class for use in calling the static method
	// that posts events to the application thread.
	jclass clazz = env->GetObjectClass(thiz);
	if (clazz == NULL) {
		ALOGE("Can't find android/media/MediaRecorder");
		jniThrowException(env, "java/lang/Exception", NULL);
		return;
	}
	mClass = (jclass) env->NewGlobalRef(clazz);

	// We use a weak reference so the MediaRecorder object can be garbage collected.
	// The reference is only used as a proxy for callbacks.
	mObject = env->NewGlobalRef(weak_thiz);
}

JNIMediaRecorderListener::~JNIMediaRecorderListener() {
	// remove global references
	JNIEnv *env = AndroidRuntime::getJNIEnv();
	env->DeleteGlobalRef(mObject);
	env->DeleteGlobalRef(mClass);
}

void JNIMediaRecorderListener::notify(int msg, int ext1, int ext2) {
	ALOGV("JNIMediaRecorderListener::notify");

	JNIEnv *env = AndroidRuntime::getJNIEnv();
	env->CallStaticVoidMethod(mClass, fields.post_event, mObject, msg, ext1,
			ext2, 0);
}

static LiveCapture* getLiveCapture(JNIEnv* env, jobject thiz) {
	Mutex::Autolock l(sLock);
	LiveCapture* const p = (LiveCapture*) env->GetIntField(thiz,
			fields.context);
	return p;
}

static void setLiveCapture(JNIEnv* env, jobject thiz,
		const LiveCapture* capture) {
	Mutex::Autolock l(sLock);
	LiveCapture* old = (LiveCapture*) env->GetIntField(thiz, fields.context);

	if (NULL != old) {
		delete old;
	}
	env->SetIntField(thiz, fields.context, (int) capture);
}

static void native_init(JNIEnv *env) {
	ALOGI("native_init");
	jclass clazz;
	clazz = env->FindClass("com/mstar/hdmirecorder/HdmiRecorder");
	if (clazz == NULL) {
		ALOGI("native_init: clazz == NULL");
		return;
	}

	fields.context = env->GetFieldID(clazz, "mNativeContext", "I");
	if (fields.context == NULL) {
		ALOGI("native_init: fields.context == NULL");
		return;
	}

	env->GetMethodID(clazz, "hello", "()V");

	fields.post_event = env->GetStaticMethodID(clazz, "postEventFromNative",
			"(Ljava/lang/Object;IIILjava/lang/Object;)V");

	//fields.post_event = env->GetMethodID(clazz, "postEventFromNative",
	//                                           "(IIILjava/lang/Object;)V");
	if (fields.post_event == NULL) {
		return;
	}
}

static void native_setup(JNIEnv *env, jobject thiz, jobject weak_this) {
	ALOGI("native_setup");

	sp<JNIMediaRecorderListener> listener = new JNIMediaRecorderListener(env,
			thiz, weak_this);

	LiveCapture *capture = new LiveCapture(listener);
	if (NULL == capture) {
		jniThrowException(env, "java/lang/RuntimeException", "Out of memory");
		return;
	}

	setLiveCapture(env, thiz, capture);
}

static jboolean native_start(JNIEnv *env, jobject thiz) {
	ALOGI("native_start");
	LiveCapture *pCap = getLiveCapture(env, thiz);
	if (NULL != pCap) {
		ALOGI("native_start NULL != pCap");
		return pCap->DApi_Capture_start();
	}

	return FALSE;
}

static jboolean native_stop(JNIEnv *env, jobject thiz) {
	ALOGI("native_stop");
	LiveCapture *pCap = getLiveCapture(env, thiz);
	if (NULL != pCap) {
		return pCap->DApi_Capture_stop();
	}

	return FALSE;
}

static void native_set_video_encoder_bitrate(JNIEnv *env, jobject thiz,
		int bitrate) {
	ALOGI("native_set_video_bitrate");
	LiveCapture *pCap = getLiveCapture(env, thiz);
	if (NULL != pCap) {
		pCap->Set_EnVideo_Bitrate(bitrate);
	}
}
//public native void native_set_video_source(int source_type);
static void native_set_video_cameraid(JNIEnv *env, jobject thiz, int cameraid) {
	ALOGI("native_set_video_cameraid");
	LiveCapture *pCap = getLiveCapture(env, thiz);
	if (NULL != pCap) {
		pCap->Set_EnVideo_cameraid(cameraid);
	}
}

static void native_set_video_wigth(JNIEnv *env, jobject thiz, int video_wight) {
	LiveCapture *pCap = getLiveCapture(env, thiz);
	if (NULL != pCap) {
		pCap->Set_Video_Wigth(video_wight);
	}
}

static void native_set_video_high(JNIEnv *env, jobject thiz, int video_hight) {
	LiveCapture *pCap = getLiveCapture(env, thiz);
	if (NULL != pCap) {
		pCap->Set_Video_High(video_hight);
	}
}

static void native_set_video_framerate(JNIEnv *env, jobject thiz,
		int video_framerate) {
	LiveCapture *pCap = getLiveCapture(env, thiz);
	if (NULL != pCap) {
		pCap->Set_Video_Framerate(video_framerate);
	}
}
static jboolean native_set_outputFD(JNIEnv *env, jobject thiz,
		jobject fileDescriptor) {
	ALOGI("native_set_outputFD in");
	if (fileDescriptor == NULL) {
		jniThrowException(env, "java/lang/IllegalArgumentException", NULL);
		return false;
	}
	int fd = jniGetFDFromFileDescriptor(env, fileDescriptor);

	LiveCapture *pCap = getLiveCapture(env, thiz);
	if (NULL != pCap) {
		//const char* params8 = env->GetStringUTFChars(filename, NULL);
		//if (params8 == NULL)
		//{
		//    ALOGE("native_set_file_path: Failed to covert jstring to String8.  This parameter will be ignored.");
		//    return;
		//}

		pCap->Set_File_Path(fd);
		return true;

		//env->ReleaseStringUTFChars(filename,params8);
	}

	return false;
}

static void native_set_video_travelingMode(JNIEnv *env, jobject thiz,
		int travelingMode) {
	LiveCapture *pCap = getLiveCapture(env, thiz);
	if (NULL != pCap) {
		pCap->Set_Video_TravelingMode(
				(EN_DAPI_CAPTURE_SOURCE_TYPE) travelingMode);
	}
}

static void native_set_video_subSource(JNIEnv *env, jobject thiz, int sub) {
	LiveCapture *pCap = getLiveCapture(env, thiz);
	if (NULL != pCap) {
		pCap->Set_Video_SubSource(sub);
	}
}

static void native_set_output_format(JNIEnv *env, jobject thiz, int format) {
	LiveCapture *pCap = getLiveCapture(env, thiz);
	if (NULL != pCap) {
		pCap->Set_Video_OutputFormat(format);
	}
}

static void native_finalize(JNIEnv *env, jobject thiz) {
	native_stop(env, thiz);
	setLiveCapture(env, thiz, 0);
}

static JNINativeMethod methods[] = {
		{ "native_start", "()Z", (void*) native_start },
		{ "native_init", "()V", (void*) native_init },
		{ "native_setup", "(Ljava/lang/Object;)V", (void*) native_setup },
		{ "native_stop", "()Z", (void*) native_stop },
		{ "native_set_video_encoder_bitrate", "(I)V", (void*) native_set_video_encoder_bitrate },
		//{"native_set_video_cameraid", "(I)V", (void*)native_set_video_cameraid},
		{ "native_set_video_wigth", "(I)V", (void*) native_set_video_wigth },
		{ "native_set_video_high", "(I)V", (void*) native_set_video_high },
		{ "native_set_video_framerate", "(I)V", (void*) native_set_video_framerate },
		{ "native_set_outputFD", "(Ljava/lang/Object;)Z", (void*) native_set_outputFD },
		{ "native_set_video_travelingMode", "(I)V", (void*) native_set_video_travelingMode },
		{"native_set_video_subSource", "(I)V", (void*) native_set_video_subSource },
		{ "native_finalize", "()V", (void*) native_finalize },
		{ "native_set_output_format", "(I)V", (void*) native_set_output_format } };

static const char* const kClassPathName = "com/mstar/hdmirecorder/HdmiRecorder";

static int registerNatives(JNIEnv* env) {
	jclass clazz;
	clazz = env->FindClass(kClassPathName);
	if (clazz == NULL) {
		ALOGE("Native registration unable to find class '%s'", kClassPathName);
		return JNI_FALSE;
	}
	if (env->RegisterNatives(clazz, methods,
			sizeof(methods) / sizeof(methods[0])) < 0) {
		ALOGE("RegisterNatives failed for '%s'", kClassPathName);
		return JNI_FALSE;
	}

	return JNI_TRUE;
}

jint JNI_OnLoad(JavaVM * pVm, void * pReserved) {
	jint result = -1;
	JNIEnv* env = NULL;

	ALOGI("JNI_OnLoad");

	if (pVm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
		ALOGE("ERROR: GetEnv failed");
		goto bail;
	}

	if (registerNatives(env) != JNI_TRUE) {
		ALOGE("ERROR: registerNatives failed");
		goto bail;
	}

	result = JNI_VERSION_1_4;

	bail: return result;
}

