from twisted.internet import protocol, reactor
from twisted.protocols import basic
from twisted.protocols.ldap import LDAPProtocol
import ldap

class SimpleLDAPServer(LDAPProtocol):
    def __init__(self):
        super().__init__()
        
        def process_request(self, request):
            if request.get_type() == ldap.RES_SEARCH_ENTRY:
                dn = "uid=testuser,dc=example,dc=com"
                attributes = {
                        'uid': ['testuser'],
                        'cn': ['Test User'],
                        'sn': ['User'],
                        }
                return self.send_search_entry(dn, attributes)
            
            elif request.get_type() == ldap.RES_BIND:
                return self.send_bind_response()
            
            return self.send_error_response(ldap.LDAP_INVALID_CREDENTIALS)
        
        class LDAPFactory(protocol.ServerFactory):
            protocol = SimpleLDAPServer
            
            if __name__ == '__main__':
                factory = LDAPFactory()
                reactor.listenTCP(389, factory)  # 기본 LDAP 포트 389에서 대기
                print("LDAP 서버가 389 포트에서 시작되었습니다.")
                reactor.run()
                

