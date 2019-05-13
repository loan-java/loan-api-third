package com.mod.loan.util;

import com.mod.loan.config.Constant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

public class jwtUtil {
	private static final Logger log = LoggerFactory.getLogger(jwtUtil.class);
	public static SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
	public static Key signingKey = null;
	static {
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(Constant.JWT_SERCETKEY);
		signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
	}

	/**
	 * 生成token
	 * 
	 * @param uid
	 * @param clientType
	 * @param clientAlias
	 * @param clientVersion
	 * @return
	 */
	public static String generToken(String uid,String phone, String clientType, String clientAlias, String clientVersion) {
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
		// 添加构成JWT的参数
		JwtBuilder builder = Jwts.builder().setHeaderParam("alg","HS256").setHeaderParam("typ", "JWT").claim("uid", uid).claim("phone", phone)
				.claim("clientType", clientType).claim("clientAlias", clientAlias).claim("clientVersion", clientVersion).setIssuedAt(now);
		builder.signWith(signatureAlgorithm, signingKey);
		// 生成JWT
		return builder.compact();
	}

	/**
	 * 解密token
	 * 
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public static Claims ParseJwt(String token) {
		try {
			Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
			return claims;
		} catch (Exception e) {
			log.error("token解析异常");
			return null;
		}

	}


}
