import cn.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;


public class CreateJwtTest {

    /***
     * 创建JWT令牌，使用私钥加密
     */
    @Test
    public void testCreateToken(){
        //证书文件路径
        String key_location="malljelly.jks";
        //秘钥库密码
        String key_password="malljelly";
        //秘钥密码
        String keypwd = "malljelly";
        //秘钥别名
        String alias = "malljelly";

        //访问证书路径 读取jks的文件
        ClassPathResource resource = new ClassPathResource(key_location);

        //创建秘钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource,key_password.toCharArray());

        //读取秘钥对(公钥、私钥)
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias,keypwd.toCharArray());

        //获取私钥
        RSAPrivateKey rsaPrivate = (RSAPrivateKey) keyPair.getPrivate();

        //自定义Payload
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("id", "1");
        tokenMap.put("name", "DOM");
        tokenMap.put("roles", "ROLE_VIP,ROLE_USER");



        //生成Jwt令牌
        Jwt jwt = JwtHelper.encode(JSONUtil.toJsonStr(tokenMap), new RsaSigner(rsaPrivate));


        //取出令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }




    /***
     * 使用公钥解密令牌数据
     */
    @Test
    public void testParseToken(){
        //令牌
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IlJPTEVfVklQLFJPTEVfVVNFUiIsIm5hbWUiOiJET00iLCJpZCI6IjEifQ.B1i912-AErQQZhfK8GBQFWxuVogMAbYW7NPh--kHfaBB2n--kZyqyFsC3Xy_W9mTFbuG63IF7cP9ik7p71fZlliZ4vFVFPRhOBEpslpp9iDkNW7aZ4mVdZk9CVYxMqrOwkK5pZhtCSB1uC3eP0sG54D_Nxpb82ApH2hRLXQAMM0pXjM13r9VXY63U3lxefczy9RZnyd6y4jLHdzQqBDeuT_31-NodDyynX6nBbZ3U0CsjYXjltT_yLwKdV0MDfTkNLi3U7UlBEwDOBAfSYOCBBIIGK3Jipx-_kh9WiLOkn-E_0xtxgH8tL9-5k9Fz8zOM4TTyhn9FFBxWGclcqNsQw";

        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0uhgJ/to1osDHgr8ppvBAD7xmeM3awjrGocpIfF8TNiWabJP8MdlburtR/IEGRYt9V6AVNrLXbV8CVDyckMRoACcXPHwX5gAY+C6hidpVOV46OX7Ti6NbaJBy45um6ZNs7qbI6d0lfrCqlpsoTkXVCh1I14sicl5OsVxIv7C5ejrlePY0xiEzboEq2HKcb4yT5no3jy7F9rhg+PSgaZxqW81ymSKP0HYSyWIYW2W4YD95st+HaNF00HZ7qnaI8AGOYmijqzNmPLn24b9JkyBtmz1G6vPsNsG4YyxXFk10nZAPwbDkxjmAVXcuQaj+AIWzZBP4tSxpoZUSE3dF1j4zQIDAQAB-----END PUBLIC KEY-----";

        //校验Jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));

        //获取Jwt原始内容 载荷
        String claims = jwt.getClaims();
        System.out.println(claims);
        //jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }
}
