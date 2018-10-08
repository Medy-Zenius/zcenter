                function map_satuan(sval){
                    switch(sval){
                        case "1":
                            return "satu";
                            break;
                        case "2":
                            return "dua";
                            break;
                        case "3":
                            return "tiga";
                            break;
                        case "4":
                            return "empat";
                            break;
                        case "5":
                            return "lima";
                            break;
                        case "6":
                            return "enam";
                            break;
                        case "7":
                            return "tujuh";
                            break;
                        case "8":
                            return "delapan";
                            break;
                        case "9":
                            return "sembilan";
                            break;
                         default:
                            return "";
                    }
                }

//1 sampai 99
                function puluhan (sval,tnol){
                    var vstr="";
                    var sufik="";
                    if (tnol){
                      vstr=map_satuan(sval.substr(1,1));
                    }
                    else {
                    if(sval.substr(0,1)=="0"){
                        vstr=map_satuan(sval.substr(1,1));
                    }
                    else {
                        var vsval0=sval.substr(0,1);
                        var vsval1=sval.substr(1,1);
                        if (vsval0=="1"){
                            if (vsval1=="1" || vsval1=="0"){
                                vstr="se";
                            }
                            else {
                                vstr=map_satuan(vsval1)+" ";
                            }
                            if(vsval1=="0"){sufik="puluh";}else{sufik="belas";}
                            vstr=vstr+sufik;
                        }
                        else {
                            vstr=map_satuan(vsval0)+" puluh " + map_satuan(vsval1);
                        }
                    }
                    }
                    return vstr;
                }

//100 sampai 900
                function ratusan(sval){
                    var vstr="";
                    if(sval=="1"){
                        vstr="seratus ";
                        }
                        else
                        {
                            vstr=map_satuan(sval);
                            if (vstr != ""){vstr=vstr+" ratus ";}
                        }
                    return vstr;
                }


//1000 sampai 99000
          function ribuan(sval,tnol){
                    var vstr="";
                    var vsval=sval;
                    if (tnol){
                        vsval=vsval.substr(1,1);
                        if (vsval=="1"){
                            vstr="seribu ";
                        }
                        else{
                            if (vsval != "0"){
                               vstr=map_satuan(vsval) + " ribu ";
                            }
                        }
                    }
                    else {
                        if (sval != "00"){
                            vstr=puluhan(sval) + " ribu ";
                        }
                    }
                    return vstr;
                }

//100.000 sd 900.000
                function ratusan_ribu(sval,xval){
                     var vstr=ratusan(sval);
                     if (xval.substr(xval.length-5,2)=="00" && vstr!=""){
                         vstr=vstr + " ribu ";
                     }
                     return vstr;
                }

//1 juta sd 99 juta
          function jutaan(sval,tnol){
                    var vstr="";
                    var vsval=sval;
                    if (tnol){
                        vsval=vsval.substr(1,1);

                        if (vsval != "0"){
                           vstr=map_satuan(vsval) + " juta ";
                        }

                    }
                    else {
                        if (sval != "00"){
                            vstr=puluhan(sval) + " juta ";
                        }
                    }
                    return vstr;
                }

//100 juta sd 900 juta
                function ratusan_juta(sval,xval){
                     var vstr=ratusan(sval);
                     if (xval.substr(xval.length-8,2)=="00" && vstr!=""){
                         vstr=vstr + " juta ";
                     }
                     return vstr;
                }

          function milyaran(sval,tnol){
                    var vstr="";
                    var vsval=sval;
                    if (tnol){
                        vsval=vsval.substr(1,1);

                        if (vsval != "0"){
                           vstr=map_satuan(vsval) + " milyar ";
                        }

                    }
                    else {
                        if (sval != "00"){
                            vstr=puluhan(sval) + " milyar ";
                        }
                    }
                    return vstr;
                }

                function ratusan_milyar(sval,xval){
                     var vstr=ratusan(sval);
                     if (xval.substr(xval.length-11,2)=="00" && vstr!=""){
                         vstr=vstr + " milyar ";
                     }
                     return vstr;
                }

          function trilyunan(sval,tnol){
                    var vstr="";
                    var vsval=sval;
                    if (tnol){
                        vsval=vsval.substr(1,1);

                        if (vsval != "0"){
                           vstr=map_satuan(vsval) + " trilyun ";
                        }

                    }
                    else {
                        if (sval != "00"){
                            vstr=puluhan(sval) + " trilyun ";
                        }
                    }
                    return vstr;
                }

                function ratusan_trilyun(sval,xval){
                     var vstr=ratusan(sval);
                     if (xval.substr(xval.length-14,2)=="00" && vstr!=""){
                         vstr=vstr + " trilyun ";
                     }
                     return vstr;
                }


                function conSubval(sval,vpos,tnol,xval){
                   switch(vpos){
                       case 1:
                          return puluhan(sval,tnol);
                          break;
                       case 2:
                          return ratusan(sval);
                          break;
                       case 3:
                          return ribuan(sval,tnol);
                          break;
                       case 4:
                          return ratusan_ribu(sval,xval);
                          break;
                       case 5:
                          return jutaan(sval,tnol);
                          break;
                       case 6:
                          return ratusan_juta(sval,xval);
                          break;
                       case 7:
                          return milyaran(sval,tnol);
                          break;
                       case 8:
                          return ratusan_milyar(sval,xval);
                          break;
                       case 9:
                          return trilyunan(sval,tnol);
                          break;
                       case 10:
                          return ratusan_trilyun(sval,xval);
                          break;
                       default:
                          return "";
                   }

                }

                function convert()
                    {
                        var x=fee.value;
                        var xl=x.length;
                        var vtxt="";
                        var tog=true;
                        var subval="";
                        var poscount=1;
                        var pengurang=2;
                        var tnol=false;
                        if (xl==1){
                           xl=2;
                           x="0"+x;
                           tnol=true;
                        }
                        while(pengurang <=  xl)
                             {
                                if(tog){
                                        subval=x.substr((xl-pengurang),2);
                                        pengurang=pengurang+1;
                                       }
                                 else {
                                     subval=x.substr((xl-pengurang),1);
                                     if((xl-pengurang)==1) {
                                         xl=xl+1;
                                         x="0"+x;
                                         tnol=true;
                                     }
                                     pengurang=pengurang+2;
                                 }


                                 vtxt=conSubval(subval,poscount,tnol,x)+vtxt;
                                 tog=!tog;
                                 poscount=poscount+1;
                              }
                         return vtxt+" rupiah";
                      }

function twochar (x)
{
  if (x.length==1)
    {
      x="0" + x;
    }
  return x;
}
