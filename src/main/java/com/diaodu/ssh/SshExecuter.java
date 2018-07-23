package com.diaodu.ssh;
import static java.lang.String.format;  
import java.io.Closeable;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.PipedInputStream;  
import java.io.PipedOutputStream;  
import com.jcraft.jsch.ChannelExec;  
import com.jcraft.jsch.ChannelShell;  
import com.jcraft.jsch.JSch;  
import com.jcraft.jsch.Session;  
import com.jcraft.jsch.UserInfo;  

public class SshExecuter implements Closeable {
	static long interval = 1000L;  
    static int timeout = 3000;  
    private SshInfo sshInfo = null;  
    private JSch jsch = null;  
    private Session session = null;  
  
    private SshExecuter( SshInfo info ) throws Exception{  
        sshInfo = info;  
        jsch = new JSch();  
        jsch.addIdentity( sshInfo.getKey() );  
        session = jsch.getSession(  sshInfo.getUser(),  
                                    sshInfo.getHost(),  
                                    sshInfo.getPort() );  
        UserInfo ui = new SshUserInfo( sshInfo.getPassPhrase() );  
        session.setUserInfo( ui );  
        session.connect();  
    }  
    public long shell( String cmd, String outputFileName )  
        throws Exception{  
        long start = System.currentTimeMillis();  
        ChannelShell channelShell = (ChannelShell)session.openChannel( "shell" );  
        PipedInputStream pipeIn = new PipedInputStream();  
        PipedOutputStream pipeOut = new PipedOutputStream( pipeIn );  
        FileOutputStream fileOut = new FileOutputStream( outputFileName );  
        channelShell.setInputStream( pipeIn );  
        channelShell.setOutputStream( fileOut );  
        channelShell.connect( timeout );  
        pipeOut.write( cmd.getBytes() );  
        Thread.sleep( interval );  
        pipeOut.close();  
        pipeIn.close();  
        fileOut.close();  
        channelShell.disconnect();  
        return System.currentTimeMillis() - start;  
    }  
    public int exec( String cmd )  
        throws Exception{  
        ChannelExec channelExec = (ChannelExec)session.openChannel( "exec" );  
        channelExec.setCommand( cmd );  
        channelExec.setInputStream( null );  
        channelExec.setErrStream( System.err );  
        InputStream in = channelExec.getInputStream();  
        channelExec.connect();  
        int res = -1;  
        StringBuffer buf = new StringBuffer( 1024 );  
        byte[] tmp = new byte[ 1024 ];  
        while ( true ) {  
            while ( in.available() > 0 ) {  
                int i = in.read( tmp, 0, 1024 );  
                if ( i < 0 ) break;  
                buf.append( new String( tmp, 0, i ) );  
            }  
            if ( channelExec.isClosed() ) {  
                res = channelExec.getExitStatus();  
                System.out.println( format( "Exit-status: %d", res ) );  
                break;  
            }  
           Thread.sleep( 100 );  
        }  
        System.out.println( buf.toString() );  
        channelExec.disconnect();  
        return res;  
    }  
    public static SshExecuter newInstance()  
        throws Exception{  
        String host = "localhost";  
        Integer port = 22;  
        String user = "hadoop";  
        String key = "./id_dsa";  
        String passPhrase = "";  
        SshInfo i = new SshInfo( host, port, user, key, passPhrase );  
        return new SshExecuter( i );  
    }  
    public Session getSession(){  
        return session;  
    }  
    public void close()  
        throws IOException{  
        getSession().disconnect();  
    }  
}  
  
class SshInfo{  
    String host = null;  
    Integer port = 22;  
    String user = null;  
    String key = null;  
    String passPhrase = null;  
    public SshInfo( String host,  
                    Integer port,  
                    String user,  
                    String key,  
                    String passPhrase ){  
        super();  
        this.host = host;  
        this.port = port;  
        this.user = user;  
        this.key = key;  
        this.passPhrase = passPhrase;  
    }  
    public String getHost(){  
        return host;  
    }  
    public Integer getPort(){  
        return port;  
    }  
    public String getUser(){  
        return user;  
    }  
    public String getKey(){  
        return key;  
    }  
    public String getPassPhrase(){  
        return passPhrase;  
    }  
}  
  
class SshUserInfo implements UserInfo{  
      
    private String passphrase = null;  
    public SshUserInfo( String passphrase ){  
        super();  
        this.passphrase = passphrase;  
    }  
    public String getPassphrase(){  
        return passphrase;  
    }  
    public String getPassword(){  
        return null;  
    }  
    public boolean promptPassphrase( String pass ){  
        return true;  
    }  
    public boolean promptPassword( String pass ){  
        return true;  
    }  
    public boolean promptYesNo( String arg0 ){  
        return true;  
    }  
    public void showMessage( String m ){  
        System.out.println( m );  
    }  

}
