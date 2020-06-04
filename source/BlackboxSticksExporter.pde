import java.nio.file.Files;
import http.requests.*;
import processing.sound.*;
import java.io.PrintStream;
import java.io.FileOutputStream;
Selection s;
IFLookAndFeel look;
File inputPath,outputPath;
int inputFilesNum=0;
XML data;
RenderManager rm;
float version;
boolean newVersion=false;
void setup(){
  size(800,600);
  final PImage icon = loadImage("assets/icon.png");
  surface.setIcon(icon);
  data = loadXML("assets/data.xml");
  if(data.getChildren("data")[0].getContent()!=""){
    inputPath=new File(data.getChildren("data")[0].getContent());
    inputFilesNum=getNumFiles(inputPath,"BFL");
  }
  if(data.getChildren("data")[1].getContent()!=""){
    outputPath=new File(data.getChildren("data")[1].getContent());
  }if(data.getChildren("data")[2].getContent()!=""){
    version = Float.parseFloat(data.getChildren("data")[2].getContent());
  }if(data.getChildren("data")[3].getContent()!=""){
    int last = Integer.parseInt(data.getChildren("data")[3].getContent());
    if(last!=hour()){
      newVersion=checkUpdates();
      data.getChildren("data")[3].setContent(hour()+"");
      saveXML(data,"assets/data.xml");
    }
  }else{
      newVersion=checkUpdates();
      data.getChildren("data")[3].setContent(hour()+"");
      saveXML(data,"assets/data.xml");
  }
  println(newVersion);
  look = new IFLookAndFeel(this,IFLookAndFeel.DEFAULT);
  look.textColor=color(0);
  if(!newVersion){
    s=new Selection(this,look);
  }
  thread("clearTemp");
  try{
    saveStrings(sketchPath()+"/assets/logs/log.txt",new String[]{""});
    saveStrings(sketchPath()+"/assets/logs/errlog.txt",new String[]{""});
    PrintStream err = new PrintStream(new FileOutputStream(sketchPath()+"/assets/logs/errlog.txt",true),true);
    System.setErr(err);
    PrintStream out = new PrintStream(new FileOutputStream(sketchPath()+"/assets/logs/log.txt",true),true);
    System.setOut(out);
  }catch(Exception e){
    e.printStackTrace();
  }
}
void draw(){
  background(50);
  if(!newVersion){
    s.show();
    if(s.active()){
    if(inputPath!=null){
      String text=inputPath.getAbsolutePath()+" | "+inputFilesNum+" Logs found!";
      textAlign(CENTER,BOTTOM);
      stroke(0);
      fill(100);
      rect(400-(textWidth(text)+20)/2,58,textWidth(text)+20,22);
      fill(0);
      text(text,400,76);
    }if(outputPath!=null){      
      textAlign(CENTER,BOTTOM);
      stroke(0);
      fill(100);
      rect(400-(textWidth(outputPath.getAbsolutePath())+20)/2,118,textWidth(outputPath.getAbsolutePath())+20,22);
      fill(0);
      text(outputPath.getAbsolutePath(),400,137);
    }
  }
  }else{
    fill(255);
    textAlign(CENTER,CENTER);
    text("Yay! New version Available!",width/2,height/2);
  }
}
void actionPerformed(GUIEvent e){
    s.actionPerformed(e);
}
void InputSelected(File selection){
  if(selection!=null){
    inputPath=selection;
    inputFilesNum=getNumFiles(inputPath,"BFL");
    data.getChildren("data")[0].setContent(selection.getAbsolutePath());
    saveXML(data,"assets/data.xml");
    println(getNumFiles(inputPath,"BFL"));
  }
}
void OutputSelected(File selection){
  if(selection!=null){
    data.getChildren("data")[1].setContent(selection.getAbsolutePath());
    saveXML(data,"assets/data.xml");
    outputPath=selection;
  }
}
int getNumFiles(File path, String ending){
  int num=0;
  File[] files = path.listFiles();
  for(int i = 0; i<files.length; i++){
    if(files[i].getName().contains(ending)){
      num++;
    }
  }
  return num;
}
void clearTemp(){
  deleteDir(new File(sketchPath()+"/temp/"));
  if(s!=null){
  s.tempClear=true;
  }
}
void deleteDir(File file) {
    File[] contents = file.listFiles();
    if (contents != null) {
        for (File f : contents) {
            if (! Files.isSymbolicLink(f.toPath())) {
                deleteDir(f);
            }
        }
    }
    file.delete();
}
void mouseDragged(){
  if(s!=null){
    s.mouseDown();
  }
}
void mousePressed(){
if(s!=null){
    s.mousePress();
  }
}
void mouseReleased(){
if(s!=null){
    s.mouseRel();
  }
}
void exit(){
  if(s!=null){
    s.stop();
  }
  surface.setVisible(false);
  delay(2000);
  clearTemp();
  super.exit();
  
}
boolean checkUpdates(){
  GetRequest get = new GetRequest("https://api.github.com/repos/bsondermann/BlackboxSticksExporter/releases/latest");
  get.send();
  
  if(Float.parseFloat(parseJSONObject(get.getContent()).getString("tag_name").trim().substring(1))>version){return true;}
  else{return false;}
}
void shutdown(){
  try{
  Runtime r = Runtime.getRuntime();
  Process proc = r.exec("shutdown -s -t 0");
  exit();
}
  catch(Exception e){}
}
