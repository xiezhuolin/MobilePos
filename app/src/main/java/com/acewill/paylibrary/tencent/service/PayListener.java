package com.acewill.paylibrary.tencent.service;

import com.acewill.paylibrary.tencent.business.ScanPayBusiness;
import com.acewill.paylibrary.tencent.protocol.pay_protocol.ScanPayResData;
import com.acewill.paylibrary.tencent.protocol.pay_query_protocol.ScanPayQueryResData;
import com.acewill.paylibrary.tencent.protocol.reverse_protocol.ReverseResData;


/**
 * Created by Administrator on 2015/9/6.
 */
public class PayListener implements ScanPayBusiness.ResultListener
{
        //API����ReturnCode���Ϸ���֧�������߼���������ϸ��⴫��ȥ��ÿһ�������Ƿ�Ϸ������ǿ�API�ܷ������
        public void onFailByReturnCodeError(ScanPayResData scanPayResData)
        {

        }

        //API����ReturnCodeΪFAIL��֧��APIϵͳ����ʧ�ܣ�����Post��API������Ƿ�淶�Ϸ�
        public void onFailByReturnCodeFail(ScanPayResData scanPayResData)
        {

        }


        //֧������API���ص����ǩ����֤ʧ�ܣ��п�����ݱ��۸���
        public void onFailBySignInvalid(ScanPayResData scanPayResData)
        {

        }


    //��ѯ����API���ص����ǩ����֤ʧ�ܣ��п�����ݱ��۸���
        public void onFailByQuerySignInvalid(ScanPayQueryResData scanPayQueryResData)
        {

        }


        //��������API���ص����ǩ����֤ʧ�ܣ��п�����ݱ��۸���
        public void onFailByReverseSignInvalid(ReverseResData reverseResData)
        {

        }


        //�û�����֧���Ķ�ά���Ѿ����ڣ���ʾ����Ա����ɨһ���û�΢�š�ˢ��������Ķ�ά��
        public void onFailByAuthCodeExpire(ScanPayResData scanPayResData)
        {

        }

        //��Ȩ����Ч����ʾ�û�ˢ��һά��/��ά�룬֮������ɨ��֧��"
        public void onFailByAuthCodeInvalid(ScanPayResData scanPayResData)
        {

        }

        //�û����㣬������֧���������ֽ�֧��
        public void onFailByMoneyNotEnough(ScanPayResData scanPayResData)
        {

        }

        //֧��ʧ��
        public void onFail(ScanPayResData scanPayResData)
        {

        }

        //֧���ɹ�
        public void onSuccess(ScanPayResData scanPayResData, String transactionID)
        {

        }
}
