LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_C_INCLUDES := \
        $(JNI_H_INCLUDE)\
        $(LOCAL_PATH) \
        $(TOP)/hardware/mstar/libutopia \
        $(TOP)/device/mstar/mstarnapoli/libraries/utopia/include \
        $(TOP)/frameworks/base/include/android_runtime \

#LOCAL_CFLAGS := -O3 -DNDEBUG

LOCAL_CXXFLAGS :=
LOCAL_CFLAGS	:= -D__cplusplus -g
LOCAL_LDFLAGS = $(LOCAL_PATH)/cameraParameter.a
LOCAL_LDLIBS := -lz -llog

LOCAL_SRC_FILES := \
		appdemo_capture.cpp \
        recorder.cpp

LOCAL_SHARED_LIBRARIES := liblog \
                          libnativehelper \
                          libcutils \
                          libutils \
                          libbinder \
                          libmedia \
                          libcamera_client \
                          libtvmanager \
                          libaudiomanager \
                          libpipmanager \
                          libandroid_runtime
                          
#LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -ldl -llog -lGLESv2 -L$(TARGET_OUT)

LOCAL_MODULE_TAGS := optional

LOCAL_MODULE    := libjni_capture
include $(BUILD_SHARED_LIBRARY)
