#
# Copyright (c) 2010-2011 MStar Semiconductor, Inc.
# All rights reserved.
#
# Unless otherwise stipulated in writing, any and all information contained
# herein regardless in any format shall remain the sole proprietary of
# MStar Semiconductor Inc. and be kept in strict confidence
# ("MStar Confidential Information") by the recipient.
# Any unauthorized act including without limitation unauthorized disclosure,
# copying, use, reproduction, sale, distribution, modification, disassembling,
# reverse engineering and compiling of the contents of MStar Confidential
# Information is unlawful and strictly prohibited. MStar hereby reserves the
# rights to any and all damages, losses, costs and expenses resulting therefrom.
#


LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_STATIC_JAVA_LIBRARIES := classes
#LOCAL_STATIC_JAVA_LIBRARIES += initAuth
LOCAL_STATIC_JAVA_LIBRARIES += FlurryAnalytics
LOCAL_JAVA_LIBRARIES += services
LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src) $(call all-renderscript-files-under, src)

LOCAL_PACKAGE_NAME := zidoo_recorder
#LOCAL_CERTIFICATE := shared
LOCAL_CERTIFICATE := platform
LOCAL_JNI_SHARED_LIBRARIES := libjni_capture

LOCAL_REQUIRED_MODULES := libjni_capture
#LOCAL_PROGUARD_FLAG_FILES := proguard.flags
LOCAL_PROGUARD_ENABLED := disabled

include $(BUILD_PACKAGE)
include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := classes:libs/classes.jar
#LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += initAuth:libs/initAuth_1.0.1.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += FlurryAnalytics:libs/FlurryAnalytics-4.2.0.jar
include $(BUILD_MULTI_PREBUILT)
include $(call all-makefiles-under,$(LOCAL_PATH))
