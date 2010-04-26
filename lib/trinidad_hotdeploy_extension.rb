module Trinidad
  module Extensions
    require File.expand_path('../../trinidad-libs/trinidad-hotdeploy-extension.jar', __FILE__)

    class HotdeployWebAppExtension < WebAppExtension
      VERSION = '0.1.0'

      def configure(tomcat, app_context)
        @options[:monitor] ||= File.join(app_context.doc_base, 'tmp/restart.txt')
        monitor = File.expand_path(options[:monitor])
        delay = @options[:delay] || 1000

        listener = org.jruby.trinidad.HotDeployLifecycleListener.new(app_context, monitor, delay)
        app_context.addLifecycleListener(listener)
      end
    end
  end
end
