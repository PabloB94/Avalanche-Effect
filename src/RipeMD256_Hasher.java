import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.DigestInputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.Security;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.util.Arrays;
	
public class RipeMD256_Hasher {
	   
	
	public byte[] hashF(byte[] input) throws Exception{
	    	
		java.security.Security.addProvider(new BouncyCastleProvider());	    
	        
	    MessageDigest hash = MessageDigest.getInstance("RIPEMD256","BC");
	    hash.update(input);
	    //System.out.println("input     : " + Utils.toHex(input));
	           
	        // input pass
	        
	    ByteArrayInputStream	bIn  = new ByteArrayInputStream(input);
	    DigestInputStream		dIn  = new DigestInputStream(bIn, hash);
	    ByteArrayOutputStream	bOut = new ByteArrayOutputStream();
	        
	    int	ch;
	    while ((ch = dIn.read()) >= 0){
	    	bOut.write(ch);
	    }
	        
	    byte[] newInput = bOut.toByteArray();
	        
	    //System.out.println("in digest : " + Utils.toHex(dIn.getMessageDigest().digest()));       
	    // output pass
	    bOut = new ByteArrayOutputStream();    
	    DigestOutputStream	dOut = new DigestOutputStream(bOut, hash);
	    dOut.write(newInput);    
	    dOut.close(); 
	    
	    //System.out.println("out digest: " + Utils.toHex(dOut.getMessageDigest().digest()));
	    return dOut.getMessageDigest().digest();  
	}
}

