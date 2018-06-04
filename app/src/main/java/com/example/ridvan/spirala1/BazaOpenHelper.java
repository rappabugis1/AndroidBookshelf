package com.example.ridvan.spirala1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class BazaOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "biblioteka.db";
    private static final int DATABASE_VERSION = 1;

    //tabela kategorija
    public static final String DATABASE_TABLE_KAT = "Kategorija";
    public static final String KATEGORIJE_ID = "_id";
    public static final String KATEGORIJE_NAZIV = "naziv";

    //tabela knjiga
    public static final String DATABASE_TABLE_KNJG = "Knjiga";
    public static final String KNJIGA_ID = "_id";
    public static final String KNJIGA_NAZIV = "naziv";
    public static final String KNJIGA_OPIS = "opis";
    public static final String KNJIGA_DATUMOBJAVLJIVANJA = "datumObjavljivanja";
    public static final String KNJIGA_BROJSTRANICA = "brojStranica";
    public static final String KNJIGA_IDWEBSERVIS = "idWebServis";
    public static final String KNJIGA_IDKATEGORIJE = "idkategorije";
    public static final String KNJIGA_SLIKA = "slika";
    public static final String KNJIGA_PREGLEDANA = "pregledana";

    //tabela autor
    public static final String DATABASE_TABLE_AUT = "Autor";
    public static final String AUTOR_ID = "_id";
    public static final String AUTOR_IME = "ime";

    //tabela autorstvo
    public static final String DATABASE_TABLE_AUTORSTVO = "Autorstvo";
    public static final String AUTORSTVO_IDAUTORA = "idautora";
    public static final String AUTORSTVO_IDKNJIGE = "idknjige";
    public static final String AUTORSTVO_ID = "_id";


    private static final String CREATE_TABLE_KATEGORIJA = "CREATE TABLE "
            + DATABASE_TABLE_KAT
            + "("
            + KATEGORIJE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KATEGORIJE_NAZIV + " TEXT"
            + ")";

    private static final String CREATE_TABLE_KNJIGA = "CREATE TABLE "
            + DATABASE_TABLE_KNJG
            + "("
            + KNJIGA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KNJIGA_NAZIV + " TEXT,"
            + KNJIGA_OPIS + " TEXT,"
            + KNJIGA_DATUMOBJAVLJIVANJA + " TEXT,"
            + KNJIGA_BROJSTRANICA + " INTEGER,"
            + KNJIGA_IDWEBSERVIS + " TEXT,"
            + KNJIGA_IDKATEGORIJE + " INTEGER,"
            + KNJIGA_SLIKA + " TEXT,"
            + KNJIGA_PREGLEDANA + " INTEGER"
            + ")";

    private static final String CREATE_TABLE_AUTOR = "CREATE TABLE "
            + DATABASE_TABLE_AUT
            + "("
            + AUTOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + AUTOR_IME + " TEXT"
            + ")";

    private static final String CREATE_TABLE_AUTORSTTVO = "CREATE TABLE "
            + DATABASE_TABLE_AUTORSTVO
            + "("
            + AUTORSTVO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + AUTORSTVO_IDAUTORA + " INTEGER,"
            + AUTORSTVO_IDKNJIGE + " INTEGER"
            + ")";


    public BazaOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_KATEGORIJA);
        db.execSQL(CREATE_TABLE_KNJIGA);
        db.execSQL(CREATE_TABLE_AUTOR);
        db.execSQL(CREATE_TABLE_AUTORSTTVO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_KAT);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_KNJG);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_AUT);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_AUTORSTVO);

        onCreate(db);
    }

    public long dodajKategoriju(String naziv) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE_KAT;
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                String nazivC = c.getString(c.getColumnIndex(KATEGORIJE_NAZIV));

                if (nazivC.equals(naziv))
                    return -1;

            } while (c.moveToNext());
        }

        ContentValues values = new ContentValues();
        values.put(KATEGORIJE_NAZIV, naziv);

        c.close();

        return db.insert(DATABASE_TABLE_KAT, null, values);
    }

    public long dodajKnjigu(Knjiga knjiga) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE_KNJG;
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                String nazivC = c.getString(c.getColumnIndex(KNJIGA_IDWEBSERVIS));

                if (nazivC.equals(knjiga.getId()))
                    return -1;
            } while (c.moveToNext());
        }
        c.close();

        ContentValues valuesKnjiga = new ContentValues();
        valuesKnjiga.put(KNJIGA_NAZIV, knjiga.getNaziv());
        valuesKnjiga.put(KNJIGA_OPIS, knjiga.getOpis());
        valuesKnjiga.put(KNJIGA_DATUMOBJAVLJIVANJA, knjiga.getDatumObjavljivanja());
        valuesKnjiga.put(KNJIGA_BROJSTRANICA, knjiga.getBrojStranica());
        valuesKnjiga.put(KNJIGA_IDWEBSERVIS, knjiga.getId());
        valuesKnjiga.put(KNJIGA_IDKATEGORIJE, dajIdKatPoImenu(knjiga.getKategorija()));
        if(knjiga.getSlika()!=null)
            valuesKnjiga.put(KNJIGA_SLIKA, knjiga.getSlika().toString());
        if(knjiga.getsSlika()!=null)
            valuesKnjiga.put(KNJIGA_SLIKA, knjiga.getsSlika().toString());

        valuesKnjiga.put(KNJIGA_PREGLEDANA, knjiga.getOznacena());

        long idKnjige = db.insert(DATABASE_TABLE_KNJG, null, valuesKnjiga);

        for (int i=0;i<knjiga.getAutori().size();i++) {
            long idAutora = dodajAutora(knjiga.autori.get(i).getImeiPrezime());

            if (idAutora == -1) {
                selectQuery = "SELECT  * FROM " + DATABASE_TABLE_AUT + " WHERE " + AUTOR_IME + " ='" + knjiga.autori.get(i).getImeiPrezime()+"'";
                c = db.rawQuery(selectQuery, null);
                if (c.moveToFirst())
                    idAutora = c.getLong(c.getColumnIndex(AUTOR_ID));
                c.close();
            }

            ContentValues valuesAutorstvo = new ContentValues();
            valuesAutorstvo.put(AUTORSTVO_IDAUTORA, idAutora);
            valuesAutorstvo.put(AUTORSTVO_IDKNJIGE, idKnjige);
            db.insert(DATABASE_TABLE_AUTORSTVO, null, valuesAutorstvo);
        }

        return idKnjige;
    }

    public long dodajAutora(String naziv) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE_AUT;
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                String nazivC = c.getString(c.getColumnIndex(AUTOR_IME));

                if (nazivC.equals(naziv))
                    return -1;

            } while (c.moveToNext());
        }

        ContentValues values = new ContentValues();
        values.put(AUTOR_IME, naziv);

        c.close();

        return db.insert(DATABASE_TABLE_AUT, null, values);
    }

    public ArrayList<Knjiga> knjigeKategorije(long idKategorije) {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<Knjiga> knjige = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE_KNJG + " WHERE " + KNJIGA_IDKATEGORIJE + " = " + idKategorije ;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                knjige.add(getKnjigaFromId(c.getLong(c.getColumnIndex(KNJIGA_ID))));
            } while (c.moveToNext());
        }

        c.close();
        return knjige;
    }

    public ArrayList<Knjiga> knjigeAutora(long idAutora) {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<Long> knjigeId= getIdKnjigaAutora(idAutora);

        ArrayList<Knjiga> knjige = new ArrayList<>();

        for (Long id:knjigeId)
            knjige.add(getKnjigaFromId(id));

        return knjige;
    }

    public Knjiga getKnjigaFromId ( long idKnjige) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE_KNJG+ " WHERE " + KNJIGA_ID + " = " + idKnjige;
        Cursor c = db.rawQuery(selectQuery, null);

        Knjiga knjiga = new Knjiga();

        if (c.moveToFirst()) {
            boolean URLOk= true;
            try {
                knjiga = new Knjiga (c.getString(c.getColumnIndex(KNJIGA_IDWEBSERVIS)),
                        c.getString(c.getColumnIndex(KNJIGA_NAZIV)),
                        getAutoriKnjige(c.getLong(c.getColumnIndex(KNJIGA_ID))),
                        c.getString(c.getColumnIndex(KNJIGA_OPIS)),
                        c.getString(c.getColumnIndex(KNJIGA_DATUMOBJAVLJIVANJA)),
                        new URL(c.getString(c.getColumnIndex(KNJIGA_SLIKA))),
                        c.getInt(c.getColumnIndex(KNJIGA_BROJSTRANICA)));
                knjiga.setOznacena(c.getInt(c.getColumnIndex(KNJIGA_PREGLEDANA))>0);
                c.close();
                return knjiga;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                URLOk=false;
            }
            if(!URLOk){
                knjiga = new Knjiga (c.getString(c.getColumnIndex(KNJIGA_IDWEBSERVIS)),
                        c.getString(c.getColumnIndex(KNJIGA_NAZIV)),
                        getAutoriKnjige(c.getLong(c.getColumnIndex(KNJIGA_ID))),
                        c.getString(c.getColumnIndex(KNJIGA_OPIS)),
                        c.getString(c.getColumnIndex(KNJIGA_DATUMOBJAVLJIVANJA)),
                        c.getString(c.getColumnIndex(KNJIGA_SLIKA)),
                        c.getInt(c.getColumnIndex(KNJIGA_BROJSTRANICA)));
                knjiga.setOznacena(c.getInt(c.getColumnIndex(KNJIGA_PREGLEDANA))>0);
                c.close();
                return knjiga;
            }
        }
        return knjiga;
    }

    public ArrayList<Autor> getAutoriKnjige (long idKnjige){
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<Autor> autori= new ArrayList<>();
        ArrayList<Long> autoriId= new ArrayList<Long>();

        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE_AUTORSTVO + " WHERE " + AUTORSTVO_IDKNJIGE + " = " + idKnjige;
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                autoriId.add(c.getLong(c.getColumnIndex(AUTORSTVO_IDAUTORA)));
            } while (c.moveToNext());
        }

        c.close();

        for (Long id: autoriId)
            autori.add(getAutor(id));

        return autori;
    }

    public ArrayList<Long> getIdKnjigaAutora (long idAutora) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE_AUTORSTVO + " WHERE " + AUTORSTVO_IDAUTORA+ " = " + idAutora;
        Cursor c = db.rawQuery(selectQuery, null);

        ArrayList<String> imena = new ArrayList<>();

        ArrayList<Long> idKnjiga = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                idKnjiga.add(c.getLong(c.getColumnIndex(AUTORSTVO_IDKNJIGE)));
            } while (c.moveToNext());
        }

        c.close();

        return idKnjiga;

    }

    public String getKnjigaNaziv (long idKnjige) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE_KNJG+ " WHERE " + KNJIGA_ID+ " = " + idKnjige;
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst())
            return c.getString(c.getColumnIndex(KNJIGA_NAZIV));

        return "Nepostojeca knjiga";
    }

    public Autor getAutor (long idAutora) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE_AUT+ " WHERE " + AUTOR_ID+ " = " + idAutora;
        Cursor c = db.rawQuery(selectQuery, null);

        Autor autor = new Autor();

        ArrayList<Long> knjigeId = getIdKnjigaAutora(idAutora);

        ArrayList<String> knjigeNazivi= new ArrayList<>();

        for (Long id: knjigeId)
            knjigeNazivi.add(getKnjigaNaziv(id));


        if (c.moveToFirst()) {
            autor = new Autor(c.getString(c.getColumnIndex(AUTOR_IME)), knjigeNazivi);
        }

        return autor;
    }

    public int oznaciKnjigu (long idKnjige){
        SQLiteDatabase db = this.getWritableDatabase();

       ContentValues values = new ContentValues();
       values.put(KNJIGA_PREGLEDANA, 1);

       return db.update(DATABASE_TABLE_KNJG, values, KNJIGA_ID + "= ?", new String[] {String.valueOf(idKnjige)});
    }

    public long dajIdAutoraPoImenu (String ime) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE_AUT+ " WHERE " + AUTOR_IME+ " = '" + ime +"'";
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            return c.getLong(c.getColumnIndex(AUTOR_ID));
        }

        return -1;
    }

    public long dajIdKnjigePoIdServisu (String idServis) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE_KNJG+ " WHERE " + KNJIGA_IDWEBSERVIS+ " = '" + idServis +"'";
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            return c.getLong(c.getColumnIndex(KNJIGA_ID));
        }

        return -1;
    }

    public ArrayList<String> dajImenaKategorija (){
        ArrayList<String> kategorije = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE_KAT;
        Cursor c = db.rawQuery(selectQuery, null);

        if(c.moveToFirst()) {
            do {
                kategorije.add(c.getString(c.getColumnIndex(KATEGORIJE_NAZIV)));
            } while (c.moveToNext());
        }
        return kategorije;
    }

    public long dajIdKatPoImenu (String imeKat){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE_KAT+ " WHERE " + KATEGORIJE_NAZIV+ " = '" + imeKat +"'";
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            return c.getLong(c.getColumnIndex(KATEGORIJE_ID));
        }

        return -1;
    }

    public ArrayList<Autor> dajAutore (){
        ArrayList<Autor> autori= new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<Knjiga> knjige = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE_AUT ;
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                autori.add(getAutor(c.getLong(c.getColumnIndex(AUTOR_ID))));
            }while(c.moveToNext());
        }

        return autori;
    }

}
