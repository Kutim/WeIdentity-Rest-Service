/*
 *       Copyright© (2019) WeBank Co., Ltd.
 *
 *       This file is part of weidentity-java-sdk.
 *
 *       weidentity-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weidentity-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weidentity-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.http;

import java.util.HashMap;
import java.util.Map;

import org.fisco.bcos.web3j.crypto.ECKeyPair;
import org.fisco.bcos.web3j.crypto.Sign;
import org.fisco.bcos.web3j.crypto.Sign.SignatureData;
import org.junit.After;
import org.junit.Before;

import com.webank.weid.http.util.TransactionEncoderUtilV2;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.HttpClient;

/**
 * @author darwindu
 **/
public abstract class BaseTest {

    @Before
    public void init() {
        System.out.println("====start testing");
    }

    @After
    public void after() {
        System.out.println("====ent testing");
    }
    
    protected static Map<String, Object> buildEncode(
        String functionName, 
        String nonce
    ) {
        Map<String, Object> arg = new HashMap<String, Object>();
        arg.put("functionName", functionName);
        arg.put("v", "1.0");

        Map<String, Object> tranMap = new HashMap<String, Object>();
        tranMap.put("nonce", nonce);
        arg.put("transactionArg", tranMap);
        return arg;
    }
    
    protected static Map<String, Object> encode(Map<String, Object> param) throws Exception {
        String functionName = param.get("functionName").toString();
        String doPost = HttpClient.doPost("http://127.0.0.1:6001/weid/api/encode", param, false);
        System.out.println(functionName + " - encode: " + doPost);
        return (Map)DataToolUtils.deserialize(doPost, HashMap.class).get("respBody");
    }
    
    protected static Map<String, Object> buildSend(
        String functionName, 
        String base64SignedMsg, 
        String data,
        String nonce
    ) {
        Map<String, Object> arg = new HashMap<String, Object>();
        arg.put("functionName", functionName);
        arg.put("v", "1.0");

        Map<String, Object> tranMap = new HashMap<String, Object>();
        tranMap.put("nonce", nonce);
        tranMap.put("data", data);
        tranMap.put("signedMessage", base64SignedMsg);
        arg.put("transactionArg", tranMap);
        
        Map<String, Object> funcMap = new HashMap<String, Object>();
        arg.put("functionArg", funcMap);
        return arg;
    }

    protected static Integer send(Map<String, Object> param) throws Exception {
        String functionName = param.get("functionName").toString();
        String doPost = HttpClient.doPost("http://127.0.0.1:6001/weid/api/transact", param, false);
        System.out.println(functionName + " - transact: " + doPost);
        return Integer.parseInt(
            DataToolUtils.deserialize(doPost, HashMap.class).get("errorCode").toString());
    }
    
    protected static String sign(
        ECKeyPair createEcKeyPair, 
        Map<String, Object> map
    ) throws Exception {
      byte[] encodedTransaction = DataToolUtils
          .base64Decode(map.get("encodedTransaction").toString().getBytes());
      SignatureData clientSignedData = Sign.getSignInterface().signMessage(
          encodedTransaction, createEcKeyPair);
      String base64SignedMsg = new String(
          DataToolUtils.base64Encode(
              TransactionEncoderUtilV2.simpleSignatureSerialization(clientSignedData)));
      return base64SignedMsg;
    }
}
