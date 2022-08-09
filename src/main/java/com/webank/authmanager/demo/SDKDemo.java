package com.webank.authmanager.demo;

import com.webank.authmanager.constant.AuthConstants;
import com.webank.authmanager.contract.AuthManager;
import com.webank.authmanager.factory.AuthManagerFactory;
import com.webank.authmanager.service.AuthByAdminService;
import com.webank.authmanager.utils.HashUtils;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.BcosSDK;

/**
 * @author aaronchu
 * @Description
 * @data 2021/01/15
 */
public class SDKDemo {
    public static void main(String[] args) throws Exception{
        BcosSDK bcosSDK = BcosSDK.build("conf/config.toml");
        Client client = bcosSDK.getClient("1");
        AuthManagerFactory authManagerFactory = new AuthManagerFactory(client);
        //Deploy contract
        AuthManager authManager = authManagerFactory.createAdmin();
        //Build facade
        AuthByAdminService authByAdminService = new AuthByAdminService(authManager);
        //Create group
        String group = "badGroup";
        authByAdminService.createGroup(group, AuthConstants.ACL_BLACKLIST_MODE);
        String blackAccount = "0x01";
        //Relate function
        String bizContractAddress = "0x2";
        String function = HashUtils.hash("add(uint256,uint256)");//业务函数签名
        authByAdminService.addFunctionToGroup(bizContractAddress, function, group);
        //Verify
        boolean canCall = authByAdminService.canCallFunction(bizContractAddress, function, blackAccount);
        System.out.println("Before blocking this account, can the account access the function?:"+canCall);

        //Add black account
        authByAdminService.addAccountToGroup(blackAccount, group);

        //Verify
        canCall = authByAdminService.canCallFunction(bizContractAddress, function, blackAccount);
        System.out.println("After blocking this account, can the account access the function?:"+canCall);

        //Remove black account
        authByAdminService.removeAccountFromGroup(blackAccount, group);

        //Verify
        canCall = authByAdminService.canCallFunction(bizContractAddress, function, blackAccount);
        System.out.println("After unblocking, can the account access the function?:"+canCall);
        System.out.println("Demo finished with success");
    }
}
