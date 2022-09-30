package com.syntepro.appbeneficiosbolivia.entity.firebase;

public class Cupon_Beneficio {

    public static final int CUPON_LAYOUT = 0;
    public static final int BENEFIT_LAYOUT = 1;

    private int viewType;

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    ////Cupon_Model
    private String idCategoria, titulo, subtitulo, precioDesc, precioReal, descripcion, tyc, cantCanje, cantCanjeUSER, fechaInicio, fechaFin, imagenCupon, imagenComercio, nombreComercio;
    private String fbCodeType = null;
    private boolean fav;

    public Cupon_Beneficio() {
    }

    public Cupon_Beneficio(int viewType, String idCategoria, String titulo, String subtitulo, String precioDesc, String precioReal, String descripcion, String tyc, String cantCanje, String cantCanjeUSER, String fechaInicio, String fechaFin, String imagenCupon, String imagenComercio, String nombreComercio, String fbCodeType, boolean fav) {
        this.viewType = viewType;
        this.idCategoria = idCategoria;
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.precioDesc = precioDesc;
        this.precioReal = precioReal;
        this.descripcion = descripcion;
        this.tyc = tyc;
        this.cantCanje = cantCanje;
        this.cantCanjeUSER = cantCanjeUSER;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.imagenCupon = imagenCupon;
        this.imagenComercio = imagenComercio;
        this.nombreComercio = nombreComercio;
        this.fbCodeType = fbCodeType;
        this.fav = fav;
    }

    public String getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(String idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }

    public String getPrecioDesc() {
        return precioDesc;
    }

    public void setPrecioDesc(String precioDesc) {
        this.precioDesc = precioDesc;
    }

    public String getPrecioReal() {
        return precioReal;
    }

    public void setPrecioReal(String precioReal) {
        this.precioReal = precioReal;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTyc() {
        return tyc;
    }

    public void setTyc(String tyc) {
        this.tyc = tyc;
    }

    public String getCantCanje() {
        return cantCanje;
    }

    public void setCantCanje(String cantCanje) {
        this.cantCanje = cantCanje;
    }

    public String getCantCanjeUSER() {
        return cantCanjeUSER;
    }

    public void setCantCanjeUSER(String cantCanjeUSER) {
        this.cantCanjeUSER = cantCanjeUSER;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getImagenCupon() {
        return imagenCupon;
    }

    public void setImagenCupon(String imagenCupon) {
        this.imagenCupon = imagenCupon;
    }

    public String getImagenComercio() {
        return imagenComercio;
    }

    public void setImagenComercio(String imagenComercio) {
        this.imagenComercio = imagenComercio;
    }

    public String getNombreComercio() {
        return nombreComercio;
    }

    public void setNombreComercio(String nombreComercio) {
        this.nombreComercio = nombreComercio;
    }

    public String getFbCodeType() {
        return fbCodeType;
    }

    public void setFbCodeType(String fbCodeType) {
        this.fbCodeType = fbCodeType;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    ////Cupon_Model

    ////Beneficio_Model
    private String documentID;
    private String btitulo;
    private String bsubtitulo;
    private String Bdescripcion;
    private String Burl;
    private String imagen;
    private String BimagenComercio;
    private String BfechaInicio;
    private String BfechaFin;
    private boolean Bfav;

    public Cupon_Beneficio(int viewType, String documentID, String btitulo, String bsubtitulo, String bdescripcion, String burl, String imagen, String bimagenComercio, String bfechaInicio, String bfechaFin, boolean bfav) {
        this.viewType = viewType;
        this.documentID = documentID;
        this.btitulo = btitulo;
        this.bsubtitulo = bsubtitulo;
        this.Bdescripcion = bdescripcion;
        this.Burl = burl;
        this.imagen = imagen;
        this.BimagenComercio = bimagenComercio;
        this.BfechaInicio = bfechaInicio;
        this.BfechaFin = bfechaFin;
        this.Bfav = bfav;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getBtitulo() {
        return btitulo;
    }

    public void setBtitulo(String btitulo) {
        btitulo = btitulo;
    }

    public String getBsubtitulo() {
        return bsubtitulo;
    }

    public void setBsubtitulo(String bsubtitulo) {
        bsubtitulo = bsubtitulo;
    }

    public String getBdescripcion() {
        return Bdescripcion;
    }

    public void setBdescripcion(String bdescripcion) {
        Bdescripcion = bdescripcion;
    }

    public String getBurl() {
        return Burl;
    }

    public void setBurl(String burl) {
        Burl = burl;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getBimagenComercio() {
        return BimagenComercio;
    }

    public void setBimagenComercio(String bimagenComercio) {
        BimagenComercio = bimagenComercio;
    }

    public String getBfechaInicio() {
        return BfechaInicio;
    }

    public void setBfechaInicio(String bfechaInicio) {
        BfechaInicio = bfechaInicio;
    }

    public String getBfechaFin() {
        return BfechaFin;
    }

    public void setBfechaFin(String bfechaFin) {
        BfechaFin = bfechaFin;
    }

    public boolean isBfav() {
        return Bfav;
    }

    public void setBfav(boolean bfav) {
        Bfav = bfav;
    }
    ////Beneficio_Model

}
