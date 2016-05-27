package uk.ac.nott.cs.g53dia.multidemo.tanker.data.exchange;

/**
 * Interface for up&downstream data exchange. Where only a less reliable transfer layer is available
 * a concrete implementation could include caching so that the tanker can still work if some packets
 * are dropped. Or the server-client architecture could be replaced with a mesh network or P2P,
 * etc... The only requirement is to implement this and the other two sub-interfaces.
 * <p>
 * Created by Barnabas on 22/03/2016.
 */
public interface DataExchange {
    UpstreamDataExchange publish();

    DownstreamDataExchange request();
}
