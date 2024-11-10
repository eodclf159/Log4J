from ldap3 import Server, Connection, ALL, MODIFY_REPLACE
from ldap3.core.exceptions import LDAPException
import logging


server = Server('192.168.0.122', get_info=ALL)  
conn = Connection(server, user='cn=admin,dc=test,dc=com', password='test', auto_bind=True)

dn = 'cn=admin,dc=test,dc=com'
attributes = {
    'objectClass': ['organizationalRole'],
    'cn': ['admin'],
    'description': ['Admin of test domain']
}

try:
    conn.add(dn, attributes=attributes)
    print("Entry added successfully!")
except LDAPException as e:
    logging.error(f"Error adding entry: {e}")

dn_to_modify = 'cn=admin,dc=test,dc=com'
modifications = {
    'description': [(MODIFY_REPLACE, ['Admin of test domain with new info'])]
}
try:
    conn.modify(dn_to_modify, modifications)
    print("Entry modified successfully!")
except LDAPException as e:
    logging.error(f"Error modifying entry: {e}")
