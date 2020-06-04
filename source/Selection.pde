import interfascia.*;
class Selection{
  Preview p;
  boolean active = false;
  boolean rendering=false;
  boolean tempClear=false;
  GUIController c1;
  IFButton settings,btnInputFolder,btnOutputFolder,btnStart,addRenderer;
  IFCheckBox checkSound, checkShutdown;
  PApplet applet;
  Settings set;
  IFLookAndFeel look;
  SoundFile sound;
  String console="";
  boolean readyToRender =false;
  
  Selection(PApplet applet, IFLookAndFeel look){
    active = true;
    this.applet = applet;
    this.look = look;
    createSelectionGui();
   sound = new SoundFile(applet,"assets/done.wav");
  }
  void show(){
    
    if(set!=null){set.show();}
    if(rendering){
      p.show();
    }
    
    textAlign(CENTER,BOTTOM);
    fill(100);
    stroke(0);
    rect(0,height-20,width,20);
    fill(0);
    text(console,width/2,height-2);
    
  }
  
  void actionPerformed(GUIEvent e){
    if(this.active){
      if(e.getSource()==settings){
        if(rm!=null&&rm.rendering){
          console="You can't open settings while rendering!";
        }else{
        println("Open Settings");
        removeSelectionGui();
        set = new Settings(applet,this.look,this);
        }  
      }
      if(e.getSource() == btnInputFolder){
        selectFolder("Select Input Folder","InputSelected");
      }
      if(e.getSource() == btnOutputFolder){
        selectFolder("Select Output Folder","OutputSelected");
      }if(e.getSource()==addRenderer){
        if(rm!=null){
          println("kot");
          rm.simultRenderNum++;
          XML xml = loadXML("assets/settings.xml");
          XML[] children = xml.getChildren("setting");
          children[9].setContent(rm.simultRenderNum+"");
          saveXML(xml,"assets/settings.xml");
        }
      }
      if(e.getSource()==btnStart){
        readyToRender = true;
        if(inputPath==null){
          console="Error: Select Input Folder!";
          readyToRender = false;
        }
        if(outputPath==null){
          console="Error: Select Output Folder!";
          readyToRender = false;
        }
        if(outputPath==null&&inputPath==null){
          console="Error: Select Input and Output Folder!";
          readyToRender = false;
        }
        if(outputPath!=null&&inputPath!=null){
          if(outputPath.getAbsolutePath().equals(inputPath.getAbsolutePath())){
            console="Error: Input Folder and Output Folder are the same!";
            readyToRender = false;
          }
        }
        if(inputPath!=null){
          if(getNumFiles(inputPath,"BFL")<=0){
            console="Error: No logs in selected Folder";
            readyToRender=false;
          }
        }
        if(!tempClear){
          console="Error: wait until temp is cleared!";
          readyToRender=false;
        }
        if(readyToRender==true&&rm==null){
          
          console="Starting...";
          rm=new RenderManager(inputPath.listFiles(),getCurrentSettings(),this);
          rendering = true;
          p = new Preview(200,rm);
        }
      }
    }
    if(set!=null){
    set.actionPerformed(e);
    }
  }
  
  boolean active(){
    return this.active;
  }
  void createSelectionGui(){
    c1 = new GUIController(applet);
    c1.setLookAndFeel(look);
    settings = new IFButton("Change Settings",50,30,100,20);
    settings.addActionListener(applet);
    btnInputFolder = new IFButton("Select Input Folder",325,30,150,20);
    btnInputFolder.addActionListener(applet);
    btnOutputFolder = new IFButton("Select Output Folder",325,90,150,20);
    btnOutputFolder.addActionListener(applet);
    btnStart = new IFButton("Start",325,150,150,20);
    btnStart.addActionListener(applet);
    checkSound = new IFCheckBox("Finish Sound",700,30);
    c1.add(checkSound);
    checkShutdown = new IFCheckBox("Shutdown",700,50);
    addRenderer = new IFButton("Add Renderer",50,90,100,20);
    addRenderer.addActionListener(applet);
    c1.add(addRenderer);
    c1.add(checkShutdown);
    c1.add(btnInputFolder);
    c1.add(btnStart);
    c1.add(btnOutputFolder);
    c1.add(settings);
    active = true;
  }
  void removeSelectionGui(){
    c1.remove(settings);
    c1.remove(btnInputFolder);
    c1.remove(btnOutputFolder);
    settings.addActionListener(null);
    btnInputFolder.addActionListener(null);
    btnOutputFolder.addActionListener(null);
    c1.setVisible(false);
    c1=null;
    settings = null;
    active=false;
  }
  
  String[]getCurrentSettings(){
    XML xml = loadXML("assets/settings.xml");
    XML[] children = xml.getChildren("setting");
    String[]settings = new String[children.length];
    for(int i = 0; i< children.length; i++){
      settings[i] = children[i].getContent();
    }
    return settings;
  }
  void mouseDown(){
    if(rendering){
      p.mouseDown();
    }
  }
  void stop(){
    if(rm!=null){
      rm.stopRender();
    }
  }
  void doneRendering(){
    if(checkShutdown.isSelected()){
      shutdown();
    }
    if(checkSound.isSelected()){
      
      sound.play();
    }
    clearTemp();
  }
  void mousePress(){
    if(set!=null){
      set.mousePress();
      
    }
  }
  void mouseRel(){
    if(set!=null){
      set.mouseRel();
      
    }
  }
}
