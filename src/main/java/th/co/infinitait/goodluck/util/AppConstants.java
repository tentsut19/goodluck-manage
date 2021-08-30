package th.co.infinitait.goodluck.util;

public class AppConstants {

    public static final Long WORK_SALE_TYPE_AAA = 1L;
    public static final Long WORK_SALE_TYPE_BBB = 2L;
    public static final Long WORK_SALE_TYPE_SURVEY = 3L;
    public static final String PATH_URL = "/api/v1/worksale/assets/";
    public static final String PATH_VIDEO_URL = "/api/v1/worksale/assets/video/";
    public static final String PATH_STREAM_URL = "/api/v1/stream/video/assets/";
    public static final String PATH_ASSETS_URL = "/api/v1/assets/";

    public enum WORK_SALE_STATUS {
        D, //DRAFT
        P, //PENDING
        NP,//NOT APPROVE QUOTATION
        CK,//CHECKING
        WA,//WAIT APPROVE
        A, //APPROVED
        R, //REJECTED
        C  //CANCELED
    }

    public enum QUOTATION_WORK_SALE_STATUS {
        D, //DRAFT
        P, //PENDING
        A, //APPROVED
        R, //REJECTED
        C,  //CANCELED
        CK
    }

    public enum WORK_SALE_ASSETS_TYPE {
        P,//PHOTO
        C,//CONTRACT
        A,//ADD DOCUMENT
        AD,//Additional documents
        PV,//VIDEO SURVEY
        V,//VIDEO
    }

    public enum IMAGE_UPLOAD_TAG {
        WORK_SALE_SURVEY
    }
    public enum ASSETS_TYPE {
        AUDIO,
        VIDEO,
        IMAGE,
        FILE,
        OTHER,
    }

    public enum REQUISITION_STATUS{
        A,//APPROVE
        D,//DRAFT
        C,//CANCEL
        F,//FINISH
    }

    public enum WITH_DRAW{
        I,//เบิกเพื่อติดตั้ง
        R,//เบิกเพื่อซ่อม
        T,//เบิกเพื่อทดสอบ
        S,//เบิกเพื่อขาย
        B,//เบิกเพื่อยืม
        SP,//เบิกเพื่อสำรอง
    }
}
