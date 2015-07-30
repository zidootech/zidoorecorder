#ifndef _APPDEMO_CAPTURE_TYPES_H_
#define _APPDEMO_CAPTURE_TYPES_H_
typedef enum
{
	EN_DAPI_CAPTURE_VIDEO_480_320,
	EN_DAPI_CAPTURE_VIDEO_640_480,
	EN_DAPI_CAPTURE_VIDEO_1280_720
}EN_DAPI_CAPTURE_VIDEO_TYPE;
typedef enum
{
	EN_DAPI_CAPTURE_SOURCE_ALL_WITH_OSD 	= 1,
	EN_DAPI_CAPTURE_SOURCE_ALL_WITHOUT_OSD	= 2,
	EN_DAPI_CAPTURE_SOURCE_SUB				= 4,
	EN_DAPI_CAPTURE_SOURCE_MAIN 			= 6,
}EN_DAPI_CAPTURE_SOURCE_TYPE;
typedef enum{
   //PIP OFF
   E_PIP_MODE_OFF = 0,
    //PIP MODE ON
   E_PIP_MODE_PIP,
   //POP MODE ON
   E_PIP_MODE_POP,
   ///PIP mode traveling, only means E_TRAVELING_2ND_VIDEO CASE
   E_PIP_MODE_TRAVELING,
   E_PIP_MODE_MAX,
}PIP_MODE;

typedef enum
{
	CAPTURE_MAIN = 0,
	CAPTURE_SUB = 1,
}CAPTURE_SOURCE;

#define E_AUDIO_PROCESSOR_SUB            1
#define E_CAPTURE_DEVICE_TYPE_DEVICE0    0
#define E_CAPTURE_MAIN_SOUND            0
#define E_CAPTURE_SUB_SOUND                1



// MStar Android Patch Begin
// The input source type
//typedef enum {
//    MAPI_INPUT_SOURCE_VGA,          ///0  <VGA input
//    MAPI_INPUT_SOURCE_ATV,          ///1 <TV input

//    MAPI_INPUT_SOURCE_CVBS,         ///2  <AV 1
//    MAPI_INPUT_SOURCE_CVBS2,        ///3  <AV 2
//    MAPI_INPUT_SOURCE_CVBS3,        ///4  <AV 3
//    MAPI_INPUT_SOURCE_CVBS4,        ///5  <AV 4
//    MAPI_INPUT_SOURCE_CVBS5,        ///6  <AV 5
//    MAPI_INPUT_SOURCE_CVBS6,        ///7  <AV 6
//    MAPI_INPUT_SOURCE_CVBS7,        ///8  <AV 7
//    MAPI_INPUT_SOURCE_CVBS8,        ///9  <AV 8
//    MAPI_INPUT_SOURCE_CVBS_MAX,     ///10 <AV max

//    MAPI_INPUT_SOURCE_SVIDEO,       ///11 <S-video 1
//    MAPI_INPUT_SOURCE_SVIDEO2,      ///12 <S-video 2
//    MAPI_INPUT_SOURCE_SVIDEO3,      ///13 <S-video 3
//    MAPI_INPUT_SOURCE_SVIDEO4,      ///14 <S-video 4
//    MAPI_INPUT_SOURCE_SVIDEO_MAX,   ///15 <S-video max

//    MAPI_INPUT_SOURCE_YPBPR,        ///16 <Component 1
//    MAPI_INPUT_SOURCE_YPBPR2,       ///17 <Component 2
//    MAPI_INPUT_SOURCE_YPBPR3,       ///18 <Component 3
//    MAPI_INPUT_SOURCE_YPBPR_MAX,    ///19 <Component max

//    MAPI_INPUT_SOURCE_SCART,        ///20 <Scart 1
//    MAPI_INPUT_SOURCE_SCART2,       ///21<Scart 2
//    MAPI_INPUT_SOURCE_SCART_MAX,    ///22 <Scart max

//    MAPI_INPUT_SOURCE_HDMI,         ///23 <HDMI 1
//    MAPI_INPUT_SOURCE_HDMI2,        ///24 <HDMI 2
//    MAPI_INPUT_SOURCE_HDMI3,        ///25 <HDMI 3
//    MAPI_INPUT_SOURCE_HDMI4,        ///26 <HDMI 4
//    MAPI_INPUT_SOURCE_HDMI_MAX,     ///27 <HDMI max

//    MAPI_INPUT_SOURCE_DTV,          ///28 <DTV

//    MAPI_INPUT_SOURCE_DVI,          ///29 <DVI 1
//    MAPI_INPUT_SOURCE_DVI2,         ///30 <DVI 2
//    MAPI_INPUT_SOURCE_DVI3,         ///31 <DVI 2
//    MAPI_INPUT_SOURCE_DVI4,         ///32 <DVI 4
//    MAPI_INPUT_SOURCE_DVI_MAX,      ///33 <DVI max

    // Application source
//    MAPI_INPUT_SOURCE_STORAGE,      ///34 <Storage
//    MAPI_INPUT_SOURCE_KTV,          ///35 <KTV
//    MAPI_INPUT_SOURCE_JPEG,         ///36 <JPEG
//    MAPI_INPUT_SOURCE_DTV2,         ///37 <DTV2
//    MAPI_INPUT_SOURCE_STORAGE2,     ///38 <Storage2
//    MAPI_INPUT_SOURCE_DTV3,         ///39 <DTV3
//    MAPI_INPUT_SOURCE_SCALER_OP,    ///40 < video from op

//    MAPI_INPUT_SOURCE_NUM,          ///41 <number of the source
//    MAPI_INPUT_SOURCE_NONE = MAPI_INPUT_SOURCE_NUM,    ///<NULL input
//} MAPI_INPUT_SOURCE_TYPE;


#endif


