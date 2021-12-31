import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SnmpGet {

    public SnmpGet() {
		// TODO Auto-generated constructor stub
	}


	/*
	 *  SnmpGet method

	 * return Response for given OID from the Device.

	 */

	public static String snmpGet(String strAddress, String community, String strOID, int snmpVersion)

	{

		String str="";

		try

		{

			OctetString community1 = new OctetString(community);

			strAddress= strAddress+"/" + 161;

			Address targetaddress = new UdpAddress(strAddress);

			TransportMapping transport = new DefaultUdpTransportMapping();

			transport.listen();

			CommunityTarget comtarget = new CommunityTarget();

			comtarget.setCommunity(community1);

			if(snmpVersion == 1){
				comtarget.setVersion(SnmpConstants.version1);
			}
			
			if(snmpVersion == 2){
				comtarget.setVersion(SnmpConstants.version2c);
			}
			
			if(snmpVersion == 3){
				comtarget.setVersion(SnmpConstants.version3);
			}
	

			comtarget.setAddress(targetaddress);

			comtarget.setRetries(2);

			comtarget.setTimeout(5000);

			PDU pdu = new PDU();

			ResponseEvent response;

			Snmp snmp;

			pdu.add(new VariableBinding(new OID(strOID)));

			pdu.setType(PDU.GET);

			snmp = new Snmp(transport);

			response = snmp.get(pdu,comtarget);

			if(response != null)

			{

				if(response.getResponse().getErrorStatusText().equalsIgnoreCase("Success"))

				{

					PDU pduresponse=response.getResponse();

					str=pduresponse.getVariableBindings().firstElement().toString();

					if(str.contains("="))

					{

						int len = str.indexOf("=");

						str=str.substring(len+1, str.length());

					}

				}

			}

			else

			{

				System.out.println("Feeling like a TimeOut occured ");

			}

			snmp.close();

		} catch(Exception e) { e.printStackTrace(); }

		System.out.println("Response="+str);

		return str;

	}
	
	
}