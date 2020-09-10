import java.io.InputStreamReader;
class Renderer extends Thread{
  File file;
  File[] logs;
  boolean running = false,stop=false;
  int numFrames, currentFrame=0;
  String[]settings;
  String currentState="";
  int number;
  int tailLength,vidWidth,bgOpacity,sticksMode,sticksModeVertPos,borderAngle,borderDistance,borderBlur;
  float borderThickness;
  float fps;
  color bgColor,sticksColor;
  PImage prevImage;
  int starttime=0;
  ImageExporter ie;
  Renderer(File f,String[]s,int number){
    currentState="Idle";
    this.number= number;
    settings = s;
    file = f;
    parseSettings();
    
  }
  void convertLog(){
    
    if(stop){return;}
    currentState=lang.getTranslation("rendererStateConverting");
    createImage(1, 1, RGB).save(sketchPath()+"/temp/"+number+"/csv/temp.png");
    new File(sketchPath()+"/temp/"+number+"/csv/temp.png").delete();
    ProcessBuilder pb;
    if(System.getProperty("os.name").contains("Mac")){
    pb = new ProcessBuilder(sketchPath()+"/assets/blackbox_decode","--merge-gps",""+file.getAbsolutePath());
    }else{
      pb = new ProcessBuilder(sketchPath()+"/assets/blackbox_decode.exe","--merge-gps",""+file.getAbsolutePath());
    }
    try {
    Process process = pb.start();
    InputStream is = process.getErrorStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    String line = reader.readLine();
    while (line !=null) {
      println(line);

      line= reader.readLine();
    }

    logs = getLogs(file);
    }catch(Exception e) {
    e.printStackTrace();
    }
  }
  void textStroke(String s,float x,float y,float stroke, int res,PGraphics g){
  PVector pos = new PVector(0,stroke);
  float da = TWO_PI/res;
  for(int i = 0; i< res; i++){
    g.text(s,x+pos.x,y+pos.y);
    pos.rotate(da);
  }
}


  void renderLog(int sublog){
    if(stop){return;}
    currentState=lang.getTranslation("rendererStateRendering");
    int w = vidWidth;
    RamTable table = new RamTable(file.getAbsolutePath());
    if(table.getRowCount()<=2){return;}
    PGraphics alphaG=createGraphics(vidWidth,vidWidth/2);
    PGraphics border=createGraphics(vidWidth,vidWidth/2);
    int startindex=1;
    float lengthus=(float(table.getRow(table.getRowCount()-1).getString(1).trim())-float(table.getRow(startindex).getString(1).trim()));
    int lengthlist = table.getRowCount()-startindex;
    numFrames=(int)((lengthus/1000000.0)*fps);
    float space=float(lengthlist)/((lengthus/1000000.0)*fps);
  int where=0;
  for (float i = startindex; i<table.getRowCount(); i+=space) {
    if(stop){return;}
    RamTableRow row = table.getRow(int(i));
    currentFrame++;
    if(borderThickness>0){
    border.beginDraw();
    border.clear();
    border.fill(0,0,0,255);
    border.stroke(0, 0, 0, 255);
    border.strokeWeight(w/400);
    //vert
    border.rect(w/2-w/30-w/6-w/400-borderThickness, ((w/3)/7.3)/2-borderThickness, w/200+borderThickness*2, w/3+borderThickness*2 );
    border.rect(w/2+w/30+w/6-w/400-borderThickness, ((w/3)/7.3)/2-borderThickness, w/200+borderThickness*2, w/3+borderThickness*2 );
    //hori
    border.rect(w/2-w/30-w/3-borderThickness, w/6-w/400+((w/3)/7.3)/2-borderThickness, w/3+borderThickness*2, w/200+borderThickness*2);
    border.rect(w/2+w/30-borderThickness, w/6-w/400+((w/3)/7.3)/2-borderThickness, w/3+borderThickness*2, w/200+borderThickness*2);
    
    //text
    border.textSize(w/25);
    if(sticksMode == 2){
      //throttle
      border.textAlign(LEFT, CENTER);
      textStroke((int)map(constrain(-(int)map(int(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500), -500, 500, 2000, 1000)+"", w/75, w/6-w/200+((w/3)/7.3)/2,borderThickness,20,border);
      //pitch
      border.textAlign(RIGHT, CENTER);
      textStroke(int(row.getString(14).trim())+"", w-w/75, w/6-w/200+((w/3)/7.3)/2,borderThickness,20,border);
      //yaw
      border.textAlign(CENTER, BOTTOM);
      textStroke(-int(row.getString(15).trim())+"", w/2-w/30-w/6-w/400, (w/2.2)-w/75,borderThickness,20,border);
      //roll
      border.textAlign(CENTER, BOTTOM);
      textStroke(int(row.getString(13).trim())+"", w/2+w/30+w/6-w/400, (w/2.2)-w/75,borderThickness,20,border);
    }else if(sticksMode ==3){
      //throttle
      border.textAlign(LEFT, CENTER);
      textStroke(int(row.getString(14).trim())+"", w/75, w/6-w/200+((w/3)/7.3)/2,borderThickness,20,border);
      //pitch
      border.textAlign(RIGHT, CENTER);
      textStroke((int)map(constrain(-(int)map(int(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500), -500, 500, 2000, 1000)+"", w-w/75, w/6-w/200+((w/3)/7.3)/2,borderThickness,20,border);
      //yaw
      border.textAlign(CENTER, BOTTOM);
      textStroke(int(row.getString(13).trim())+"", w/2-w/30-w/6-w/400, (w/2.2)-w/75,borderThickness,20,border);
      //roll
      border.textAlign(CENTER, BOTTOM);
      textStroke(-int(row.getString(15).trim())+"", w/2+w/30+w/6-w/400, (w/2.2)-w/75,borderThickness,20,border);
    
    }if(sticksMode == 1){
      //throttle
      border.textAlign(LEFT, CENTER);
      textStroke(int(row.getString(14).trim())+"", w/75, w/6-w/200+((w/3)/7.3)/2,borderThickness,20,border);
      //pitch
      border.textAlign(RIGHT, CENTER);
      textStroke((int)map(constrain(-(int)map(int(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500), -500, 500, 2000, 1000)+"", w-w/75, w/6-w/200+((w/3)/7.3)/2,borderThickness,20,border);
      //yaw
      border.textAlign(CENTER, BOTTOM);
      textStroke(-int(row.getString(15).trim())+"", w/2-w/30-w/6-w/400, (w/2.2)-w/75,borderThickness,20,border);
      //roll
      border.textAlign(CENTER, BOTTOM);
      textStroke(int(row.getString(13).trim())+"", w/2+w/30+w/6-w/400, (w/2.2)-w/75,borderThickness,20,border);
    }if(sticksMode == 4){
      //throttle
      border.textAlign(LEFT, CENTER);
      textStroke((int)map(constrain(-(int)map(int(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500), -500, 500, 2000, 1000)+"", w/75, w/6-w/200+((w/3)/7.3)/2,borderThickness,20,border);
      //pitch
      border.textAlign(RIGHT, CENTER);
      textStroke(int(row.getString(14).trim())+"", w-w/75, w/6-w/200+((w/3)/7.3)/2,borderThickness,20,border);
      //yaw
      border.textAlign(CENTER, BOTTOM);
      textStroke(int(row.getString(13).trim())+"", w/2-w/30-w/6-w/400, (w/2.2)-w/75,borderThickness,20,border);
      //roll
      border.textAlign(CENTER, BOTTOM);
      textStroke(-int(row.getString(15).trim())+"", w/2+w/30+w/6-w/400, (w/2.2)-w/75,borderThickness,20,border);
    }
    
    

    border.noStroke();
    float taillength=(fps/30)*tailLength;
    float prevx=0;
    float prevy=0;
    float prevx1=0;
    float prevy1=0;
    for (float j = 0; j< space*taillength; j++) {
      RamTableRow trailrow = table.getRow(constrain(int(i-j), 0, table.getRowCount()-1));
      border.fill(0,0,0,255);
      float r = map(j, 0, space*taillength, ((w/3)/7.3)/1.5, ((w/3)/7.3)/10)+borderThickness*2;
      float x=0, y=0, x1=0, y1=0;
      if (sticksMode==2) {
        //yaw
        x =map(-int(trailrow.getString(15).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3);
        //throttle
        y = map(-(int)map(int(trailrow.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3)/2;
        //roll
        x1= map(int(trailrow.getString(13).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3);
        //pitch
        y1 = map(-int(trailrow.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3)/2;
      }
      if (sticksMode==3) {
        //yaw
        x =map(int(trailrow.getString(13).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3);
        //throttle
        y = map(-int(trailrow.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3)/2;
        //roll
        x1= map(-int(trailrow.getString(15).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3);
        //pitch
        y1 = map(-(int)map(int(trailrow.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3)/2;
      }
      if (sticksMode==1) {
        //yaw
        x =map(-int(trailrow.getString(15).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3);
        //throttle
        y = map(-int(trailrow.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3)/2;
        //roll
        x1= map(int(trailrow.getString(13).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3);
        //pitch
        y1 = map(-(int)map(int(trailrow.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3)/2;
      }
      if (sticksMode==4) {
        //yaw
        x =map(int(trailrow.getString(13).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3);
        //throttle
        y = map(-(int)map(int(trailrow.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3)/2;
        //roll
        x1= map(-int(trailrow.getString(15).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3);
        //pitch
        y1 = map(-int(trailrow.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3)/2;
      }
      if (x!=prevx||y!=prevy) {
        border.ellipse(x, y, r, r);
        prevx=x;
        prevy=y;
      }
      if (x1!=prevx1||y1!=prevy1) {
        border.ellipse(x1, y1, r, r);
        prevx1=x1;
        prevy1 =y1;
      }
      trailrow=null;
    }
    border.fill(0,0,0,255);
    
    border.textAlign(CENTER,CENTER);
    if(sticksMode==2||sticksMode==4){
    textStroke("Mode"+sticksMode,w/2-w/30-w/6-w/400,map(sticksModeVertPos,0,100,((w/3)/7.3)/2,(((w/3)/7.3)/2)+w/3),borderThickness,20,border);
    }else{
    textStroke("Mode"+sticksMode,w/2+w/30+w/6-w/400,map(sticksModeVertPos,0,100,((w/3)/7.3)/2,(((w/3)/7.3)/2)+w/3),borderThickness,20,border);
    }
    border.fill(0,0,0, 255);
    if (sticksMode==2) {
      border.ellipse(map(-int(row.getString(15).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3), map(-(int)map(int(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3)/2, (w/3)/7.3+borderThickness*2, (w/3)/7.3+borderThickness*2);
      border.ellipse(map(int(row.getString(13).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3), map(-int(row.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3)/2, (w/3)/7.3+borderThickness*2, (w/3)/7.3+borderThickness*2);
    }else if(sticksMode==3){
      border.ellipse(map(int(row.getString(13).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3), map(-int(row.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3)/2, (w/3)/7.3+borderThickness*2, (w/3)/7.3+borderThickness*2);
      border.ellipse(map(-int(row.getString(15).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3), map(-(int)map(int(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3)/2, (w/3)/7.3+borderThickness*2, (w/3)/7.3+borderThickness*2);
    
      }
    else if(sticksMode==1){
       border.ellipse(map(-int(row.getString(15).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3), map(-int(row.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3)/2, (w/3)/7.3+borderThickness*2, (w/3)/7.3+borderThickness*2);
      border.ellipse(map(int(row.getString(13).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3), map(-(int)map(int(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3)/2, (w/3)/7.3+borderThickness*2, (w/3)/7.3+borderThickness*2);
    }if (sticksMode==4) {
      border.ellipse(map(int(row.getString(13).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3), map(-(int)map(int(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3)/2, (w/3)/7.3+borderThickness*2, (w/3)/7.3+borderThickness*2);
      border.ellipse(map(-int(row.getString(15).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3), map(-int(row.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3)/2, (w/3)/7.3+borderThickness*2, (w/3)/7.3+borderThickness*2);
    }
    border.endDraw();
    
    
    }
    alphaG.beginDraw();
    //alphaG.translate(xoff, yoff);
    alphaG.clear();
    alphaG.fill(255, 255, 255, 255);
    alphaG.stroke(0, 0, 0, 255);
    alphaG.strokeWeight(w/400);
    //vert
    alphaG.rect(w/2-w/30-w/6-w/400, ((w/3)/7.3)/2, w/200, w/3 );
    alphaG.rect(w/2+w/30+w/6-w/400, ((w/3)/7.3)/2, w/200, w/3 );
    //hori
    alphaG.rect(w/2-w/30-w/3, w/6-w/400+((w/3)/7.3)/2, w/3, w/200);
    alphaG.rect(w/2+w/30, w/6-w/400+((w/3)/7.3)/2, w/3, w/200);

    //text
    alphaG.textSize(w/25);
    if(sticksMode == 2){
      //throttle
      alphaG.textAlign(LEFT, CENTER);
      alphaG.text((int)map(constrain(-(int)map(int(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500), -500, 500, 2000, 1000)+"", w/75, w/6-w/200+((w/3)/7.3)/2);
      //pitch
      alphaG.textAlign(RIGHT, CENTER);
      alphaG.text(int(row.getString(14).trim())+"", w-w/75, w/6-w/200+((w/3)/7.3)/2);
      //yaw
      alphaG.textAlign(CENTER, BOTTOM);
      alphaG.text(-int(row.getString(15).trim())+"", w/2-w/30-w/6-w/400, (w/2.2)-w/75);
      //roll
      alphaG.textAlign(CENTER, BOTTOM);
      alphaG.text(int(row.getString(13).trim())+"", w/2+w/30+w/6-w/400, (w/2.2)-w/75);
    }else if(sticksMode ==3){
      //throttle
      alphaG.textAlign(LEFT, CENTER);
      alphaG.text(int(row.getString(14).trim())+"", w/75, w/6-w/200+((w/3)/7.3)/2);
      //pitch
      alphaG.textAlign(RIGHT, CENTER);
      alphaG.text((int)map(constrain(-(int)map(int(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500), -500, 500, 2000, 1000)+"", w-w/75, w/6-w/200+((w/3)/7.3)/2);
      //yaw
      alphaG.textAlign(CENTER, BOTTOM);
      alphaG.text(int(row.getString(13).trim())+"", w/2-w/30-w/6-w/400, (w/2.2)-w/75);
      //roll
      alphaG.textAlign(CENTER, BOTTOM);
      alphaG.text(-int(row.getString(15).trim())+"", w/2+w/30+w/6-w/400, (w/2.2)-w/75);
    
    }if(sticksMode == 1){
      //throttle
      alphaG.textAlign(LEFT, CENTER);
      alphaG.text(int(row.getString(14).trim())+"", w/75, w/6-w/200+((w/3)/7.3)/2);
      //pitch
      alphaG.textAlign(RIGHT, CENTER);
      alphaG.text((int)map(constrain(-(int)map(int(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500), -500, 500, 2000, 1000)+"", w-w/75, w/6-w/200+((w/3)/7.3)/2);
      //yaw
      alphaG.textAlign(CENTER, BOTTOM);
      alphaG.text(-int(row.getString(15).trim())+"", w/2-w/30-w/6-w/400, (w/2.2)-w/75);
      //roll
      alphaG.textAlign(CENTER, BOTTOM);
      alphaG.text(int(row.getString(13).trim())+"", w/2+w/30+w/6-w/400, (w/2.2)-w/75);
    }if(sticksMode == 4){
      //throttle
      alphaG.textAlign(LEFT, CENTER);
      alphaG.text((int)map(constrain(-(int)map(int(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500), -500, 500, 2000, 1000)+"", w/75, w/6-w/200+((w/3)/7.3)/2);
      //pitch
      alphaG.textAlign(RIGHT, CENTER);
      alphaG.text(int(row.getString(14).trim())+"", w-w/75, w/6-w/200+((w/3)/7.3)/2);
      //yaw
      alphaG.textAlign(CENTER, BOTTOM);
      alphaG.text(int(row.getString(13).trim())+"", w/2-w/30-w/6-w/400, (w/2.2)-w/75);
      //roll
      alphaG.textAlign(CENTER, BOTTOM);
      alphaG.text(-int(row.getString(15).trim())+"", w/2+w/30+w/6-w/400, (w/2.2)-w/75);
    }
    
    

    alphaG.noStroke();
    float taillength=(fps/30)*tailLength;
    float prevx=0;
    float prevy=0;
    float prevx1=0;
    float prevy1=0;
    for (float j = 0; j< space*taillength; j++) {
      RamTableRow trailrow = table.getRow(constrain(int(i-j), 0, table.getRowCount()-1));
      alphaG.fill(175, 175, 175, map(j, 0, space*taillength, 100, 0));
      float r = map(j, 0, space*taillength, ((w/3)/7.3)/1.5, ((w/3)/7.3)/10);
      float x=0, y=0, x1=0, y1=0;
      if (sticksMode==2) {
        //yaw
        x =map(-int(trailrow.getString(15).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3);
        //throttle
        y = map(-(int)map(int(trailrow.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3)/2;
        //roll
        x1= map(int(trailrow.getString(13).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3);
        //pitch
        y1 = map(-int(trailrow.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3)/2;
      }
      if (sticksMode==3) {
        //yaw
        x =map(int(trailrow.getString(13).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3);
        //throttle
        y = map(-int(trailrow.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3)/2;
        //roll
        x1= map(-int(trailrow.getString(15).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3);
        //pitch
        y1 = map(-(int)map(int(trailrow.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3)/2;
      }
      if (sticksMode==1) {
        //yaw
        x =map(-int(trailrow.getString(15).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3);
        //throttle
        y = map(-int(trailrow.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3)/2;
        //roll
        x1= map(int(trailrow.getString(13).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3);
        //pitch
        y1 = map(-(int)map(int(trailrow.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3)/2;
      }
      if (sticksMode==4) {
        //yaw
        x =map(int(trailrow.getString(13).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3);
        //throttle
        y = map(-(int)map(int(trailrow.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3)/2;
        //roll
        x1= map(-int(trailrow.getString(15).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3);
        //pitch
        y1 = map(-int(trailrow.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3)/2;
      }
      if (x!=prevx||y!=prevy) {
        alphaG.ellipse(x, y, r, r);
        prevx=x;
        prevy=y;
      }
      if (x1!=prevx1||y1!=prevy1) {
        alphaG.ellipse(x1, y1, r, r);
        prevx1=x1;
        prevy1 =y1;
      }
      trailrow=null;
    }
    alphaG.fill(255,255,255,255);
    alphaG.textAlign(CENTER,CENTER);
    if(sticksMode==2||sticksMode==4){
    alphaG.text("Mode"+sticksMode,w/2-w/30-w/6-w/400,map(sticksModeVertPos,0,100,((w/3)/7.3)/2,(((w/3)/7.3)/2)+w/3));
    }else{
    alphaG.text("Mode"+sticksMode,w/2+w/30+w/6-w/400,map(sticksModeVertPos,0,100,((w/3)/7.3)/2,(((w/3)/7.3)/2)+w/3));
    }
    alphaG.fill(sticksColor, 255);
    if (sticksMode==2) {
      alphaG.ellipse(map(-int(row.getString(15).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3), map(-(int)map(int(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3)/2, (w/3)/7.3, (w/3)/7.3);
      alphaG.ellipse(map(int(row.getString(13).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3), map(-int(row.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3)/2, (w/3)/7.3, (w/3)/7.3);
    }else if(sticksMode==3){
      alphaG.ellipse(map(int(row.getString(13).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3), map(-int(row.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3)/2, (w/3)/7.3, (w/3)/7.3);
      alphaG.ellipse(map(-int(row.getString(15).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3), map(-(int)map(int(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3)/2, (w/3)/7.3, (w/3)/7.3);
    
      }
    else if(sticksMode==1){
       alphaG.ellipse(map(-int(row.getString(15).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3), map(-int(row.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3)/2, (w/3)/7.3, (w/3)/7.3);
      alphaG.ellipse(map(int(row.getString(13).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3), map(-(int)map(int(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3)/2, (w/3)/7.3, (w/3)/7.3);
    }if (sticksMode==4) {
      alphaG.ellipse(map(int(row.getString(13).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3), map(-(int)map(int(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3)/2, (w/3)/7.3, (w/3)/7.3);
      alphaG.ellipse(map(-int(row.getString(15).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3), map(-int(row.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3)/2, (w/3)/7.3, (w/3)/7.3);
    }
    alphaG.endDraw();
    
    PGraphics out = createGraphics(alphaG.width, alphaG.height, JAVA2D);
    out.beginDraw();
    out.clear();
    if (bgOpacity>0) {
      out.background(bgColor, bgOpacity);
    }
    float scl= 0.01;
    if (borderThickness>0) {
      PVector v = PVector.fromAngle(radians(borderAngle));
      v.setMag(borderDistance);
      out.image(border, vidWidth*scl*2+v.x,vidWidth*scl+v.y,vidWidth - vidWidth*scl*4,vidWidth/2 - vidWidth*scl*2);
      out.filter(BLUR,(float(borderBlur)/100)*((vidWidth/75.0f)/3));
    }
    out.image(alphaG,vidWidth*scl*2,vidWidth*scl,vidWidth - vidWidth*scl*4,vidWidth/2 - vidWidth*scl*2);

    out.endDraw();
    
    ie.addImage(out,"temp/"+number+"/"+sublog+"/Images/line_"+("0000000000"+where).substring((where+"").length())+".png");
    prevImage = out;
    where++;
    row = null;
  }
  table = null;
  }
  void compileLog(int sublog){
    if(stop){return;}
    currentState=lang.getTranslation("rendererStateCompiling");
    ProcessBuilder processBuilder;
    if(System.getProperty("os.name").contains("Mac")){
    processBuilder = new ProcessBuilder(sketchPath()+"/assets/ffmpeg", "-r", fps+"", "-i", sketchPath()+"/temp/"+number+"/"+sublog+"/Images/line_%010d.png", "-vcodec", "prores_ks","-pix_fmt", "yuva444p10le", "-profile:v", "4444", "-q:v", "20", "-r", fps+"", "-y", outputPath.getAbsolutePath()+"/"+file.getName().substring(0,file.getName().length()-4)+".mov");
    }else{
    processBuilder = new ProcessBuilder(sketchPath()+"/assets/ffmpeg.exe", "-r", fps+"", "-i", sketchPath()+"/temp/"+number+"/"+sublog+"/Images/line_%010d.png", "-vcodec", "prores_ks","-pix_fmt", "yuva444p10le", "-profile:v", "4444", "-q:v", "20", "-r", fps+"", "-y", outputPath.getAbsolutePath()+"/"+file.getName().substring(0,file.getName().length()-4)+".mov");
    }
  try {
    Process process = processBuilder.start();
    InputStream is = process.getErrorStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    String line = reader.readLine();
    while (line !=null) {
      if (line.contains("frame")&&line.charAt(0)=='f') {
        String s = line+"";
        s = s.replaceAll("[A-Z,a-z,=]","");
        String []split = s.split(" ");
        String out="";
        for(int i = 0; i<split.length; i++){
          if(out.equals("")){out=split[i];}
        }        
        currentFrame=Integer.parseInt(out);
      }
      println(line);
      line= reader.readLine();
    }
  }
  catch(Exception e) {
  }
  }
  void startRender(){
    running=true;
    start();
  }
  void parseSettings(){
    fps=Float.parseFloat(settings[0]);
    tailLength=Integer.parseInt(settings[1]);
    vidWidth=Integer.parseInt(settings[2]);
    borderThickness=map(Integer.parseInt(settings[3]),0,100,0,(vidWidth/75.0f));
    borderAngle=Integer.parseInt(settings[4]);
    borderDistance=(int)map(Integer.parseInt(settings[5]),0,100,0,vidWidth/40);
    borderBlur=Integer.parseInt(settings[6]);
    bgOpacity=Integer.parseInt(settings[8]);
    sticksMode=Integer.parseInt(settings[9]);
    sticksModeVertPos=Integer.parseInt(settings[10]);
    bgColor = color(Integer.parseInt(settings[7].substring(1, 3), 16), Integer.parseInt(settings[7].substring(3, 5), 16), Integer.parseInt(settings[7].substring(5, 7), 16));
    sticksColor = color(Integer.parseInt(settings[11].substring(1, 3), 16), Integer.parseInt(settings[11].substring(3, 5), 16), Integer.parseInt(settings[11].substring(5, 7), 16));
    
  }
  @Override void run(){
    starttime=millis();
    if(stop){return;}
    convertLog();
    
    if(stop){return;}
        
        for(int i = 0; i< logs.length; i++){
          starttime=millis();
          file = logs[i];
          ie = new ImageExporter();
          renderLog(i);
          while(ie.queue.size()>0){
          try{Thread.sleep(10);}catch(Exception e){}}
          ie.done=true;
          starttime=millis();
          compileLog(i);
        }
    

    deleteDir(new File(sketchPath()+"/temp/"+number+"/"));
    running=false;
    currentState="Done";
  }
  int getETA(){
    float percent = float(currentFrame)/numFrames;
    int dt = millis()-starttime;
    return (int)max(((dt/percent-millis()+starttime)/1000),0);
  }
  int getRuntime(){
    return (int)((millis()-starttime)/1000);
  }
  void stopRender(){
    stop=true;
  }
  File[] getLogs(File f){
    File[] files = new File(f.getParent()).listFiles();
    LinkedList<File> files2 = new LinkedList<File>();
    for(int i = 0; i< files.length;i++){
      if(files[i].getAbsolutePath().contains(".csv")&&files[i].getAbsolutePath().contains(f.getName().substring(0,f.getName().length()-4))){

        File del = new File(files[i].getAbsolutePath().substring(0,files[i].getAbsolutePath().length()-4)+".event");
        del.delete();
        del = new File(files[i].getAbsolutePath().substring(0,files[i].getAbsolutePath().length()-4)+".gps.csv");
        del.delete();
        del = new File(files[i].getAbsolutePath().substring(0,files[i].getAbsolutePath().length()-4)+".gps.gpx");
        del.delete();
        files[i].renameTo(new File(sketchPath()+"/temp/"+number+"/csv/"+files[i].getName()));
        files[i] = new File(sketchPath()+"/temp/"+number+"/csv/"+files[i].getName());
        files2.add(files[i]);
      }
    }
    files = new File[files2.size()];
    for(int i = 0; i< files.length;i++){
      files[i] = files2.get(i);
    }
    return files;
  }
}
