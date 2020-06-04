import java.io.InputStreamReader;
class Renderer extends Thread{
  File file;
  boolean running = false,stop=false;
  int numFrames, currentFrame=0;
  String[]settings;
  String currentState="";
  int number;
  int fps,tailLength,vidWidth,borderThickness,bgOpacity,sticksMode,sticksModeVertPos;
  color bgColor,sticksColor;
  PImage prevImage;
  int starttime=0;
  Renderer(File f,String[]s,int number){
    currentState="Idle";
    this.number= number;

    settings = s;
    file = f;
    parseSettings();
  }
  void convertLog(){
    
    if(stop){return;}
    currentState="converting";
    createImage(1, 1, RGB).save(sketchPath()+"/temp/"+number+"/csv/temp.png");
    new File(sketchPath()+"/temp/"+number+"/csv/temp.png").delete();
    ProcessBuilder pb = new ProcessBuilder(sketchPath()+"/assets/blackbox_decode.exe","--merge-gps",""+file.getAbsolutePath());
    try {
    Process process = pb.start();
    InputStream is = process.getErrorStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    String line = reader.readLine();
    while (line !=null) {
      println(line);

      line= reader.readLine();
    }
    File del = new File(file.getAbsolutePath().substring(0,file.getAbsolutePath().length()-4)+".01.event");
    del.delete();
    del = new File(file.getAbsolutePath().substring(0,file.getAbsolutePath().length()-4)+".01.gps.csv");
    del.delete();
    file =new File(file.getAbsolutePath().substring(0,file.getAbsolutePath().length()-4)+".01.csv");
    file.renameTo(new File(sketchPath()+"/temp/"+number+"/csv/"+file.getName()));
    file = new File(sketchPath()+"/temp/"+number+"/csv/"+file.getName());
    Table table = loadTable(file.getAbsolutePath(),"header");
    float lengthus=(int(table.getRow(table.getRowCount()-1).getString(1).trim())-int(table.getRow(1).getString(1).trim()))*1.002305;
    numFrames=(int)((lengthus/1000000f)*fps);
    
    }catch(Exception e) {
    e.printStackTrace();
    }
  }
  void renderLog(){
    
    if(stop){return;}
    currentState="Rendering";
    int w = vidWidth;
    Table table = loadTable(file.getAbsolutePath(),"header");
    PGraphics alphaG=createGraphics(vidWidth,vidWidth/2);
    int startindex=1;
    float lengthus=(float(table.getRow(table.getRowCount()-1).getString(1).trim())-float(table.getRow(startindex).getString(1).trim()))*1.002305;
    int lengthlist = table.getRowCount()-startindex;
    numFrames=(int)((lengthus/1000000.0)*float(fps));
    float space=float(lengthlist)/((lengthus/1000000.0)*float(fps));
  int where=0;
  for (float i = startindex; i<table.getRowCount(); i+=space) {
    if(stop){return;}
    TableRow row = table.getRow(int(i));
    currentFrame++;
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
      TableRow trailrow = table.getRow(constrain(int(i-j), 0, table.getRowCount()-1));
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
      if (x!=prevx&&y!=prevy) {
        alphaG.ellipse(x, y, r, r);
        prevx=x;
        prevy=y;
      }
      if (x1!=prevx1&&y1!=prevy1) {
        alphaG.ellipse(x1, y1, r, r);
        prevx1=x1;
        prevy1 =y1;
      }
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
    PImage border = createImage(alphaG.width, alphaG.height, ARGB);
    if (borderThickness>0) {
      border.loadPixels();
      for (int j = 0; j<border.pixels.length; j++) {
        border.pixels[j]=0;
        float m=9999;
        for (int x = -borderThickness; x<borderThickness; x++) {
          if(m!=0){
            for (int y = -borderThickness; y<borderThickness; y++) {
              if(m!=0){
                if(((alphaG.pixels[constrain(j+x+(y*alphaG.width),0,border.pixels.length-1)]>>24)&0xFF)!=0){
                  m=min(dist(0,0,x,y),m);
                }
              }
            }
          }
        }
        border.pixels[j] = ((int)(map(constrain(m,0,float(borderThickness)*sqrt(2)),float(borderThickness)*sqrt(2),0,0,255))<<24);
      }
      border.updatePixels();
    }

    PGraphics out = createGraphics(alphaG.width, alphaG.height, JAVA2D);
    out.beginDraw();
    out.clear();
    if (bgOpacity>0) {
      out.background(bgColor, bgOpacity);
    }
    if (borderThickness>0) {

      out.image(border, 0,0);
    }
    out.image(alphaG, 0, 0);

    out.endDraw();
    out.save("temp/"+number+"/Images/line_"+("0000000000"+where).substring((where+"").length())+".png"); 

    prevImage = out;
    where++;
  }
  
  }
  void compileLog(){
    if(stop){return;}
    currentState="Compiling";
    ProcessBuilder processBuilder = new ProcessBuilder(sketchPath()+"/assets/ffmpeg.exe", "-r", fps+"", "-i", sketchPath()+"/temp/"+number+"/Images/line_%010d.png", "-vcodec", "prores_ks","-pix_fmt", "yuva444p10le", "-profile:v", "4444", "-q:v", "20", "-r", fps+"", "-y", outputPath.getAbsolutePath()+"/"+file.getName().substring(0,file.getName().length()-4)+".mov");
  try {
    Process process = processBuilder.start();
    InputStream is = process.getErrorStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    String line = reader.readLine();
    while (line !=null) {
      if (line.contains("frame")) {
        currentFrame=Integer.parseInt(line.substring(7, 13).replaceAll("[\\D]", ""));
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
    fps=Integer.parseInt(settings[0]);
    tailLength=Integer.parseInt(settings[1]);
    vidWidth=Integer.parseInt(settings[2]);
    borderThickness=Integer.parseInt(settings[3]);
    bgOpacity=Integer.parseInt(settings[5]);
    sticksMode=Integer.parseInt(settings[6]);
    sticksModeVertPos=Integer.parseInt(settings[7]);
    bgColor = color(Integer.parseInt(settings[4].substring(1, 3), 16), Integer.parseInt(settings[4].substring(3, 5), 16), Integer.parseInt(settings[4].substring(5, 7), 16));
    sticksColor = color(Integer.parseInt(settings[8].substring(1, 3), 16), Integer.parseInt(settings[8].substring(3, 5), 16), Integer.parseInt(settings[8].substring(5, 7), 16));
    
  }
  @Override void run(){
    starttime=millis();
    if(stop){return;}
    convertLog();
    
    if(stop){return;}
        starttime=millis();
    renderLog();
    if(stop){return;}
        starttime=millis();
    compileLog();
    currentState="Done";
    
    running=false;
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
}
