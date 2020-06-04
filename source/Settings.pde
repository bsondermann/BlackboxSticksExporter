
class Settings{
  boolean active = false;
  GUIController c;
  PApplet applet;
  XML xml;
  String explanations[]={"Frames per Second of the final Video.\nDefault: 60, Recommended: 30, 60, 120","Traillength sets the length of the Trail behind the Stick","Width determens the width of the final Video.\nThe hight is automatically calculated with a ratio of 2:1","BorderThickness sets the width of a 'Shadow' surrounding the contours\n(increases render time a lot). Values: 0 to 100","BackgroundColor sets the color of the entire background.\nValues: #000000 to #FFFFFF (Use a online colorpicker and copy the hex color)","BackgroundOpacity sets the transparency of the Background.\nValues: 0 (100% transparent) to 100 (0% transparent)","SticksMode sets the sticks mode of the final Video.\nValues: 1 to 4","SticksModeVertPos sets the vertical position of the sticks mode information.\nThe horizontal position is automatically determined and on the throttle stick.\nValues: 0 (top) to 100 (bottom)","StickColor sets the color of the sticks.\nValues: #000000 to #FFFFFF (Use a online colorpicker and copy the hex color)","SimultaneousRenders sets the maximum number of simultaneous renderers."};
  IFLabel labels[];
  String labelstext[];
  IFLabel explain;
  IFTextField fields[];
  IFLookAndFeel look;
  IFButton back;
  IFButton save;
  IFButton defaults;
  Selection selection;
  ColorWheel cw;
  Settings(PApplet applet, IFLookAndFeel look,Selection selection){
    this.applet = applet;
    this.look = look;
    this.selection = selection;
    
    c = new GUIController(applet);
    c.setLookAndFeel(look);
    initXML();
    createSettingsGui();
    
  }
  
  boolean active(){return this.active;}
  void show(){
    explain.setLabel("");

    if(labels.length!=0){
      fill(100);
      rect(40,70,720,30*labels.length+10);
      for(int i =0; i< labels.length-1; i++){
        line(50,105+30*i,750,105+30*i);
      }
      for(int i = 0; i<fields.length;i++){
        fill(0);
        textAlign(LEFT,TOP);
        text(labelstext[i],50,85+30*i);
        
      }
      for(int i = 0; i<fields.length;i++){

        if(labels[i].isMouseOver(mouseX,mouseY)){
          fill(255);
          rectMode(CORNER);
          stroke(0);
          rect(5+mouseX,mouseY-5,textWidth(explanations[i])+10,textHeight(explanations[i])+20);
          explain.setLabel(explanations[i]);
          explain.setPosition(10+mouseX,mouseY);
        }
      }
      
    }
    if(cw!=null){
      stroke(0);
      fill(100);
      rect(cw.posx-20,cw.posy-20,cw.w+40,cw.w+40);
      cw.show();
      strokeWeight(1);
      
    }
  }
  void actionPerformed(GUIEvent e){
    if(this.active){
    if(e.getSource()==back){
      removeSettingsGui();
    }else if(e.getSource()==save){
      checkColor();
      writeXML();
   }else if(e.getSource()==defaults){
     XML[] newchildren=loadXML("assets/data.xml").getChildren("setting");
     XML[] children = xml.getChildren("setting");
     for(int i = 0; i< children.length; i++){
       children[i].setContent(newchildren[i].getContent());
     }
     saveXML(xml,"assets/settings.xml");
     initXML();
   }else{
      IFTextField temp = (IFTextField)e.getSource();
      String type = temp.getLabel();
      String content = temp.getValue();
      if(type.contains("num")){
        String[]args=type.split(",");
        content = content.replaceAll("[^0-9]","");
        int numcontent = Integer.parseInt(content);
        if(args.length==3){
          numcontent = constrain(Integer.parseInt(content),Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        }else if(args.length==2){
          numcontent = max(Integer.parseInt(content),Integer.parseInt(args[1]));
        }
        temp.setValue(""+abs(numcontent));
      }else if(type.contains("col")){
        if(!content.contains("#")){
          content = "#"+content;
        }
        content = content.toUpperCase();
        content = content.replaceAll("[^A-F0-9#]","");
        content = content.substring(0,Math.min(content.length(),7));
        temp.setValue(content);
      }
   }}
    
  }
  void createSettingsGui(){
    back = new IFButton("Back",50,30,100,20);
    back.addActionListener(applet);
    save = new IFButton("Save",width-150,30,100,20);
    save.addActionListener(applet);
    explain = new IFLabel("",-100,-100);
    defaults = new IFButton("Load Defaults",width/2-50,30,100,20);
    defaults.addActionListener(applet);
    c.add(defaults);
    c.add(back);
    c.add(save);
        c.add(explain);
    active = true;
  }
  void removeSettingsGui(){
    for(int i = 0; i< labels.length;i++){
      c.remove(labels[i]);
      c.remove(fields[i]);
    }
    c.remove(save);
    save.addActionListener(null);
    save=null;
    fields=new IFTextField[0];
    labels = new IFLabel[0];
    c.remove(back);
    back.addActionListener(null);
    back=null;
    c.setVisible(false);
    c=null;
    active = false;
    selection.createSelectionGui();
    cw=null;
  }
  void initXML(){
    
    xml=loadXML("assets/settings.xml");
    XML[] children = xml.getChildren("setting");
    labelstext = new String[children.length]; 
    labels = new IFLabel[children.length];
    fields = new IFTextField[children.length];
    for(int i = 0; i< children.length;i++){
      labels[i] = new IFLabel("",50,85+30*i);
      labels[i].setWidth(150);
      labels[i].setHeight(20);
      labelstext[i] = children[i].getString("id").trim();
      c.add(labels[i]);
      fields[i] = new IFTextField(children[i].getString("type").trim(),600,80+30*i,150);
      fields[i].setValue( children[i].getContent());
      fields[i].addActionListener(applet);
      c.add(fields[i]);
      //labels[i].setLabel(children[i].getString("id").trim());
    }
  }
  void writeXML(){
    XML[] children = xml.getChildren("setting");
    for(int i = 0; i<children.length;i++){
      children[i].setContent(fields[i].getValue());
    }
    saveXML(xml,"assets/settings.xml");
    s.console = "Settings saved!";
  }
  void checkColor(){
    for(int i = 0; i< fields.length;i++){
      if(fields[i].getValue().contains("#")){
        String cont = fields[i].getValue();
        int l = cont.length();
        
        for(int j = 0; j<7-l;j++){
          cont=cont+"0";
        }
        fields[i].setValue(cont);
      }
    }
  }
  int textHeight(String text){
    return (text.split("\n", -1).length-1)*18;
  }
  void mousePress(){
    if(mouseButton == 39){
      for(int i = 0; i< fields.length; i++){
        if(fields[i].getLabel().contains("col")&&fields[i].isMouseOver(mouseX,mouseY)){
          cw = new ColorWheel(200,width/2-100,mouseY-100,this,fields[i]);
        }
      }
    }
    if(cw!=null){
      cw.mousePress();
    }
  }
  void mouseRel(){
    if(cw!=null){
      cw.mouseRel();
    }
  }
  void disableCW(){
    cw=null;
  }
}
