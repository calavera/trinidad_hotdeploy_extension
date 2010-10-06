require File.expand_path(File.dirname(__FILE__) + '/spec_helper')
require 'optparse'

describe Trinidad::Extensions::HotdeployWebAppExtension do
  subject { Trinidad::Extensions::HotdeployWebAppExtension.new({}) }

  before(:each) do
    @tomcat = Trinidad::Tomcat::Tomcat.new
    @context = Trinidad::Tomcat::StandardContext.new
    @context.doc_base = Dir.pwd
  end

  it "uses tmp/restart.txt as monitor by default" do
    listener = subject.configure(@tomcat, @context)
    listener.monitor.should =~ /tmp\/restart.txt$/
  end

  it "can use a custom monitor file" do
    ext = Trinidad::Extensions::HotdeployWebAppExtension.new({
      :monitor => 'tmp/redeploy.txt'
    })
    listener = ext.configure(@tomcat, @context)
    listener.monitor.should =~ /tmp\/redeploy.txt$/
  end

  it "uses the base directory when the custom path is relative" do
    ext = Trinidad::Extensions::HotdeployWebAppExtension.new({
      :monitor => 'tmp/redeploy.txt'
    })
    listener = ext.configure(@tomcat, @context)
    listener.monitor.should =~ /^#{File.expand_path('../../', __FILE__)}/
  end

  it "checks the file each second by default" do
    listener = subject.configure(@tomcat, @context)
    listener.delay.should == 1000
  end

  it "can use a custom delay" do
    ext = Trinidad::Extensions::HotdeployWebAppExtension.new({
      :delay => 30000
    })
    listener = ext.configure(@tomcat, @context)
    listener.delay.should == 30000
  end
end

describe Trinidad::Extensions::HotdeployOptionsExtension do
  it "allows to specify a command line option to load the hotdeploy" do
    parser = OptionParser.new
    options = {}

    subject.configure(parser, options)

    options[:extensions].keys.should include(:hotdeploy)
  end
end
