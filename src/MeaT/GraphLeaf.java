package MeaT;

import blockchain.Transaction;
import graph.Edge;

import java.util.List;

public class GraphLeaf {
/**
 * Represents a Merkle Graph Tree leaf
 */

    // The data to be stored in this node
    private final List<Edge> edges;

    /**
     * Initialises the leaf node, which consists
     * of the specified block of data.
     *
     * transactions Data block to be placed in the leaf node
     */
    public GraphLeaf(final List<Edge> edges)
    {
        this.edges = edges;
    }

    /**
     * @return The data block associated with this leaf node
     */
    public List<Edge> getEdges()
    {
        return (edges);
    }

    /**
     * Returns a string representation of the specified
     * byte array, with the values represented in hex. The
     * values are comma separated and enclosed within square
     * brackets.
     *
     * @param array The byte array
     *
     * @return Bracketed string representation of hex values
     */
    private String toHexString(final byte[] array)
    {
        final StringBuilder str = new StringBuilder();

        str.append("[");

        boolean isFirst = true;
        for(int idx=0; idx<array.length; idx++)
        {
            final byte b = array[idx];

            if (isFirst)
            {
                //str.append(Integer.toHexString(i));
                isFirst = false;
            }
            else
            {
                //str.append("," + Integer.toHexString(i));
                str.append(",");
            }

            final int hiVal = (b & 0xF0) >> 4;
            final int loVal = b & 0x0F;
            str.append((char) ('0' + (hiVal + (hiVal / 10 * 7))));
            str.append((char) ('0' + (loVal + (loVal / 10 * 7))));
        }

        str.append("]");

        return(str.toString());
    }

    /**
     * Returns a string representation of the data block
     */
    public String toString()
    {
        final StringBuilder str = new StringBuilder();

        for(Edge edge: edges)
        {
            str.append(toHexString(edge.getId()));
        }
        return(str.toString());
    }

}

