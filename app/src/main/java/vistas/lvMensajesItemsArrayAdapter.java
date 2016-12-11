package vistas;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.school.toolinfodoc.R;
import java.util.ArrayList;

public class lvMensajesItemsArrayAdapter extends BaseAdapter {

    ArrayList<lvMensajesItems> items;
    Context c;
    LayoutInflater inflater;
    static final int ROW_DOC = 0;
    static final int ROW_REP = 1;
    static final int HEADER = 2;
    static final int STATUS_SENT = 0;
    static final int STATUS_RECEIVED = 1;
    static final int STATUS_READ = 2;

    public lvMensajesItemsArrayAdapter(Context c, ArrayList<lvMensajesItems> items){
        this.c = c;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemViewType(int position) {
        lvMensajesItems item = (lvMensajesItems)getItem(position);

        if (item.header.equals("")){
            return item.tipo == 0 ? ROW_DOC : ROW_REP;
        }
        return HEADER;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        int type = getItemViewType(pos);
        lvMensajesItems msj = (lvMensajesItems)getItem(pos);
        TextView lblFechaHora;
        TextView lblMensaje;
        ImageView imgStatus;
        TextView lblHeader;


        if (convertView == null){
            inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            switch (type){
                case ROW_DOC:
                    convertView = inflater.inflate(R.layout.lvmensajefromdocitem,null);
                    break;

                case ROW_REP:
                    convertView = inflater.inflate(R.layout.lvmensajefromrepitem,null);
                    break;

                case HEADER:
                    convertView = inflater.inflate(R.layout.lvmensajeheaderitem,null);
                    break;

                default:
                    break;
            }
        }

        switch (type){
            case ROW_DOC:
                lblFechaHora = (TextView)convertView.findViewById(R.id.lblFechaHora);
                lblMensaje = (TextView)convertView.findViewById(R.id.lblMensaje);

                lblFechaHora.setText(msj.getFechaHora());
                lblMensaje.setText(msj.getMensaje());

                break;

            case ROW_REP:
                lblFechaHora = (TextView)convertView.findViewById(R.id.lblFechaHora);
                lblMensaje = (TextView)convertView.findViewById(R.id.lblMensaje);
                imgStatus = (ImageView)convertView.findViewById(R.id.imgStatus);

                lblFechaHora.setText(msj.getFechaHora());
                lblMensaje.setText(msj.getMensaje());

                switch (msj.getStatus()){
                    case STATUS_SENT:
                        imgStatus.setImageResource(R.drawable.sent_status_icon);
                        break;
                    case STATUS_RECEIVED:
                        imgStatus.setImageResource(R.drawable.received_status_icon);
                        break;
                    case STATUS_READ:
                        imgStatus.setImageResource(R.drawable.read_status_icon);
                        break;
                    default:
                        break;
                }

                break;

            case HEADER:
                lblHeader = (TextView)convertView.findViewById(R.id.lblHeader);
                lblHeader.setText(msj.getHeader());
                break;

            default:
                break;
        }

        return convertView;
    }
}
