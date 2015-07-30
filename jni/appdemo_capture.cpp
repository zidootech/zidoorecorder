#include <camera/Camera.h>
#include <camera/ICamera.h>
#include <media/mediarecorder.h>
#include <camera/CameraParameters.h>
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
#include "jni.h"

#define LOG_TAG "liveCapture"

using namespace android;

#if 0
#define log() printf("\033[31m [arex.huang_log][%s][%d] \033[m \n",__FUNCTION__,__LINE__)
#endif
#ifdef __cplusplus
extern "C" {
#endif

extern sp<Camera> CreateCamera(int &cameraId, int width, int height,
		int frameRate, int travelingMode, int subSource);

LiveCapture::LiveCapture(sp<MediaRecorderListener> listener) {
	mCameraid = 5;
	mVideoWigth = 1280;
	mVideoHigh = 720;
	mBitrate = 100000;
	mVideoFramerate = 20;
	bRecAuido = FALSE;
	EN_DAPI_CAPTURE_VIDEO_TYPE mEnVideoType = EN_DAPI_CAPTURE_VIDEO_1280_720;
	EN_DAPI_CAPTURE_SOURCE_TYPE mEnTravelingMode = EN_DAPI_CAPTURE_SOURCE_MAIN;
	mFileName = NULL;
	mFilePoint = -1;
	sp < Camera > mLivecapCamera = NULL;
	sp < MediaRecorder > mLivecapMediaRecorder = NULL;
	mSubSource = MAPI_INPUT_SOURCE_NONE;
	mFormat = 8;

	mMediaRecorderListener = listener;

}
LiveCapture::~LiveCapture() {
	mCameraid = 0;
	mVideoWigth = 0;
	mVideoHigh = 0;
	mBitrate = 0;
	mVideoFramerate = 0;
	bRecAuido = FALSE;
	EN_DAPI_CAPTURE_VIDEO_TYPE mEnVideoType = EN_DAPI_CAPTURE_VIDEO_1280_720;
	EN_DAPI_CAPTURE_SOURCE_TYPE mEnTravelingMode = EN_DAPI_CAPTURE_SOURCE_MAIN;
	mFileName = NULL;
	mFilePoint = -1;
	sp < Camera > mLivecapCamera = NULL;
	sp < MediaRecorder > mLivecapMediaRecorder = NULL;
}

void LiveCapture::Set_File_Path(int fd) {
	ALOGI("LiveCapture::Set_File_Path fd = %d", fd);
	if (fd > 0) {
		mFilePoint = fd;
	}
	return;
}
bool LiveCapture::Open_File() {
	if (mFilePoint > 0) {
		return TRUE;
	}
	if (mFileName == NULL) {
		ALOGE("Open_File: FileName == NULL ");
		return FALSE;
	}
	mFilePoint = open(mFileName, O_RDWR | O_CREAT, 0777);

	if (mFilePoint < 0) {
		ALOGE("LiveCapture::Open_File open mFileName = %s failed", mFileName);
		perror(mFileName);
		return FALSE;
	}
	return TRUE;
}
bool LiveCapture::Close_File() {
	bool bRet;
	if (mFilePoint >= 0) {
		bRet = close(mFilePoint);
		if (bRet < 0) {
			perror(mFileName);
			return FALSE;
		}
		mFilePoint = -1;
	}
	return TRUE;
}
/*void LiveCapture::Set_EnVideo_Source(CAPTURE_SOURCE source_type)
 {
	 switch(source_type)
	 {
		 case CAPTURE_MAIN:
		 {
		 	 mEnTravelingMode = EN_DAPI_CAPTURE_SOURCE_MAIN;
		 }
		 break;
		 case CAPTURE_SUB:
		 {
		 	 mEnTravelingMode = EN_DAPI_CAPTURE_SOURCE_SUB;
		 }
		 break;
		 case 2:
		 {
		 	 mEnTravelingMode = EN_DAPI_CAPTURE_SOURCE_ALL_WITHOUT_OSD;
		 }
		 break;
		 default:
		 {
		 	 mEnTravelingMode = EN_DAPI_CAPTURE_SOURCE_ALL_WITH_OSD;
		 }
		 break;

 	 }
 	 return;
 }*/

void LiveCapture::Set_EnVideo_cameraid(int cameraid) {
	mCameraid = cameraid;
}

void LiveCapture::Set_Video_Wigth(int video_wight) {
	mVideoWigth = video_wight;
}

void LiveCapture::Set_Video_High(int video_hight) {
	mVideoHigh = video_hight;
}

void LiveCapture::Set_EnVideo_Bitrate(int bitrate) {
	if (bitrate < 800000) {
		bitrate = 1024000;
	}
	mBitrate = bitrate;
}

void LiveCapture::Set_RecAuido(bool RecAuido) {
	bRecAuido = RecAuido;
}

void LiveCapture::Set_Video_Framerate(int video_framerate) {
	mVideoFramerate = video_framerate;
}

void LiveCapture::Set_Video_OutputFormat(int format) {
	mFormat = format;
}

bool LiveCapture::DApi_Capture_start(void) {
	bool bRet = FALSE;
	int useCameraId = 6;

	Set_RecAuido(TRUE);

	ALOGV("DApi_Capture_start 2 in");

	do {
		mLivecapCamera = CreateCamera(useCameraId, mVideoWigth, mVideoHigh,
				mVideoFramerate, mEnTravelingMode, mSubSource);

		if (mLivecapCamera == NULL) {
			ALOGE("DApi_Capture_start: CreateCamera failed");
			break;
		}

		mLivecapCamera->unlock();
		mLivecapMediaRecorder = new MediaRecorder();

		mLivecapMediaRecorder->setListener(mMediaRecorderListener);

		bRet = mLivecapMediaRecorder->setCamera(mLivecapCamera->remote(),
				mLivecapCamera->getRecordingProxy());
		if (bRet != NO_ERROR) {
			ALOGE("DApi_Capture_start: mLivecapMediaRecorder->setCamera error");
			break;
		}

		bRet = mLivecapMediaRecorder->setVideoSource(VIDEO_SOURCE_CAMERA);
		if (bRet != NO_ERROR) {
			break;
		}

		if (bRecAuido) {
			bRet = mLivecapMediaRecorder->setAudioSource(1); //AUDIO_SOURCE_MIC = 1;AUDIO_SOURCE_REMOTE_SUBMIX = 8
			if (bRet != NO_ERROR) {
				break;
			}
		}

		bRet = mLivecapMediaRecorder->setOutputFormat(mFormat); //(8);//( 10 );//8 is ts

		if (bRet != NO_ERROR) {
			break;
		}

		bRet = mLivecapMediaRecorder->setOutputFile(mFilePoint, 0, 0);
		if (bRet != NO_ERROR) {
			break;
		}

		bRet = mLivecapMediaRecorder->setVideoEncoder(VIDEO_ENCODER_H264);
		if (bRet != NO_ERROR) {
			break;
		}

		if (bRecAuido) {
			bRet = mLivecapMediaRecorder->setAudioEncoder(AUDIO_ENCODER_AAC); //( AUDIO_ENCODER_AAC );//AUDIO_SOURCE_MIC = 1;AUDIO_SOURCE_REMOTE_SUBMIX = 8
			if (bRet != NO_ERROR) {
				break;
			}
		}
		String8 cameraidstr;/* ("video-param-camera-id=5");*/
		if (5 == useCameraId) {
			cameraidstr = "video-param-camera-id=5";
		} else if (6 == useCameraId) {
			cameraidstr = "video-param-camera-id=6";
		} else if (7 == useCameraId) {
			cameraidstr = "video-param-camera-id=7";
		}
		bRet = mLivecapMediaRecorder->setParameters(cameraidstr);
		if (bRet != NO_ERROR) {
			break;
		}

		bRet = mLivecapMediaRecorder->setVideoSize(mVideoWigth, mVideoHigh);
		if (bRet != NO_ERROR) {
			break;
		}
		bRet = mLivecapMediaRecorder->setVideoFrameRate(mVideoFramerate);
		if (bRet != NO_ERROR) {
			break;
		}

		char strVieobitratval[100] = { 0 };
		snprintf(strVieobitratval, 100, "video-param-encoding-bitrate=%u",
				mBitrate);
		String8 strVideobitrate(strVieobitratval);
		bRet = mLivecapMediaRecorder->setParameters(strVideobitrate);
		if (bRet != NO_ERROR) {
			break;
		}

		if (bRecAuido) {
			snprintf(strVieobitratval, 100, "audio-param-sampling-rate=%u",
					44100);
			String8 audioSampleRate(strVieobitratval);
			mLivecapMediaRecorder->setParameters(audioSampleRate);

			snprintf(strVieobitratval, 100, "audio-param-number-of-channels=%u",
					2);
			String8 audioChannels(strVieobitratval);
			mLivecapMediaRecorder->setParameters(audioChannels);

			snprintf(strVieobitratval, 100, "audio-param-encoding-bitrate=%u",
					96000);
			String8 audioEncodingBitrate(strVieobitratval);
			mLivecapMediaRecorder->setParameters(audioEncodingBitrate);
		}

		bRet = mLivecapMediaRecorder->prepare();
		if (bRet != NO_ERROR) {
			ALOGE("DApi_Capture_start: mLivecapMediaRecorder->prepare error");
			break;
		}

		bRet = mLivecapMediaRecorder->start();
		if (bRet != NO_ERROR) {
			ALOGE("DApi_Capture_start: mLivecapMediaRecorder->start error");
			break;
		}
		ALOGV("DApi_Capture_start: TRUE exit ");
		return TRUE;
	} while (FALSE);

	ALOGV("DApi_Capture_start: FALSE exit");
	DApi_Capture_stop();
	return FALSE;
}

bool LiveCapture::DApi_Capture_stop(void) {
	if (mLivecapMediaRecorder != NULL) {
		mLivecapMediaRecorder->stop();
		mLivecapMediaRecorder->release();
		mLivecapMediaRecorder.clear();
		mLivecapMediaRecorder = NULL;
	}

	if (mLivecapCamera != NULL) {
		mLivecapCamera->lock();
		mLivecapCamera->disconnect();
		mLivecapCamera.clear();
		mLivecapCamera = NULL;
	}
	//sleep(1);
	//return Close_File();
	return TRUE;

}

void LiveCapture::Set_Video_TravelingMode(
		EN_DAPI_CAPTURE_SOURCE_TYPE travelingMode) {
	mEnTravelingMode = travelingMode;
}

void LiveCapture::Set_Video_SubSource(int sub) {
	mSubSource = sub;
}

#ifdef __cplusplus
}
#endif

