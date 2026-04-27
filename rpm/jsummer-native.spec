#
# build file for java version rpm package
# (c) 2006 Klaus Zerwes zero-sys.net
# 
# TODO:
#	dynamic version handling
#	fix requires - todo: swt
#	TODO: splitt file in header define and build
#	only the define-part should differ from distro to distro
#

# norootforbuild 
# neededforbuild  ant javac gcj


Summary: 	compute and check MD5, SHA-160 and SHA-256 message digest
Name: 		jsummer-native
Version: 	0.0.5.2
Release: 	1
#Copyright: 	(c) 2006 Klaus Zerwes zero-sys.net GPL
License:	GPL
Group: 		Productivity/File utilities
Source: 	http://zero-sys.net/
Vendor: 	zero-sys.net
URL: 		http://zero-sys.net/
BuildRoot: 	%{_tmppath}/%{name}-%{version}-build
Provides:	jsummer
Conflicts:	jsummer-java

# start sysdependent
BuildRequires: 	ant java swt-jni-gtk swt-java  swt-native gcc-java
Requires:	swt-jni-gtk swt-native

%define prefix /usr
#%define binprefix /usr/X11 # SuSE
%define binprefix %{prefix}
%define swtjar %{prefix}/share/java/swt-zerosys-3139-gtk.jar
# end sysdep

%description
A Tool to compute and check MD5, SHA-160 and SHA-256 message digest.
Console- and GUI-version

Author:
(c) 2005 - 2006 Klaus Zerwes zero-sys.net


%prep
rm -rf $RPM_BUILD_DIR/JSummer-%{version}
zcat $RPM_SOURCE_DIR/JSummer-%{version}-src.tar.gz | tar -xf -


%build
cd JSummer-%{version}
cd build
chmod 755 rescompile.sh
ant -Dpackage.swt.jar=%{swtjar} \
    -Dpackage.swtsharedlibpath=/usr/%{_lib} \
    -Dpackage.swtsharedlibname=swt-zerosys-3139-gtk \
    native-shared-gui
ant native-console


%install
pwd
rm -rf $RPM_BUILD_ROOT
mkdir -p $RPM_BUILD_ROOT%{binprefix}/bin
mkdir -p $RPM_BUILD_ROOT%{prefix}/share
mkdir -p $RPM_BUILD_ROOT%{prefix}/share/pixmaps
mkdir -p $RPM_BUILD_ROOT%{prefix}/share/applications
mkdir -p $RPM_BUILD_ROOT%{prefix}/share/man/man1
mkdir -p $RPM_BUILD_ROOT%{prefix}/share/mime-info
mkdir -p $RPM_BUILD_ROOT%{prefix}/share/mime/packages
mkdir -p $RPM_BUILD_ROOT%{prefix}/share/mimelnk/application
mkdir -p $RPM_BUILD_ROOT%{prefix}/share/apps/konqueror/servicemenus
mkdir -p $RPM_BUILD_ROOT%{prefix}/share/application-registry

cd JSummer-%{version}
#pwd
install -m 644 debian/jsummer.1 $RPM_BUILD_ROOT%{prefix}/share/man/man1/jsummer.1
install -m 644 icon/JSummer.xpm $RPM_BUILD_ROOT%{prefix}/share/pixmaps
install -m 644 rpm/jsummer.desktop $RPM_BUILD_ROOT%{prefix}/share/applications
install -m 644 debian/JSummerX.desktop $RPM_BUILD_ROOT%{prefix}/share/applications
install -m 644 debian/jsummer.mime $RPM_BUILD_ROOT%{prefix}/share/mime-info
install -m 644 debian/jsummer.keys $RPM_BUILD_ROOT%{prefix}/share/mime-info
install -m 644 debian/jsummer.xml $RPM_BUILD_ROOT%{prefix}/share/mime/packages
install -m 644 debian/digest-*.desktop $RPM_BUILD_ROOT%{prefix}/share/mimelnk/application
install -m 644 debian/allfiles.desktop $RPM_BUILD_ROOT%{prefix}/share/apps/konqueror/servicemenus
install -m 644 debian/jsummer.applications $RPM_BUILD_ROOT%{prefix}/share/application-registry
install -m 755 build/out/JSummer $RPM_BUILD_ROOT%{binprefix}/bin/
install -m 755 build/out/JSummerX $RPM_BUILD_ROOT%{binprefix}/bin/
ln -sv %{binprefix}/bin/JSummer $RPM_BUILD_ROOT%{binprefix}/bin/jsummer
ln -sv %{binprefix}/bin/JSummerX $RPM_BUILD_ROOT%{binprefix}/bin/jsummerx



%clean
rm -rf $RPM_BUILD_ROOT


%post
if [ -x "`which update-menus 2>/dev/null`" ]; then
	update-menus
fi
if [ -x "`which update-mime 2>/dev/null`" ]; then
	update-mime
fi
if [ -x "`which update-mime-database 2>/dev/null`" ]; then
	update-mime-database /usr/share/mime
fi
if [ -x "`which update-desktop-database 2>/dev/null`" ]; then
    update-desktop-database
fi
if [ -d /opt/kde3/share/mimelnk/application ]; then
	for i in digest-md5.desktop digest-sha160.desktop digest-sha256.desktop; do
		[ -f /opt/kde3/share/mimelnk/application/$i ] || \
			ln -s %{prefix}/share/mimelnk/application/$i /opt/kde3/share/mimelnk/application/
	done
fi
if [ -d /opt/kde3/share/apps/konqueror/servicemenus ]; then
	for i in allfiles.desktop; do
		[ -f /opt/kde3/share/apps/konqueror/servicemenus/$i ] || \
			ln -s %{prefix}/share/apps/konqueror/servicemenus/$i /opt/kde3/share/apps/konqueror/servicemenus/
	done
fi
if [ -d /opt/gnome/share/application-registry ]; then
	for i in jsummer.applications; do
		[ -f /opt/gnome/share/application-registry/$i ] || \
			ln -s %{prefix}/share/application-registry/$i /opt/gnome/share/application-registry/
	done
fi
if [ -d /opt/gnome/share/mime-info ]; then
	for i in jsummer.mime jsummer.keys; do
		[ -f /opt/gnome/share/mime-info/$i ] || \
			ln -s %{prefix}/share/mime-info/$i /opt/gnome/share/mime-info/
	done
fi

%postun
if [ -x "`which update-menus 2>/dev/null`" ]; then
	update-menus
fi
if [ -x "`which update-mime 2>/dev/null`" ]; then
	update-mime
fi
if [ -x "`which update-mime-database 2>/dev/null`" ]; then
	update-mime-database /usr/share/mime
fi
if [ -x "`which update-desktop-database 2>/dev/null`" ]; then
    update-desktop-database
fi
if [ -d /opt/kde3/share/mimelnk/application ]; then
	for i in digest-md5.desktop digest-sha160.desktop digest-sha256.desktop; do
		[ -L /opt/kde3/share/mimelnk/application/$i ] && \
			rm /opt/kde3/share/mimelnk/application/$i
	done
fi
if [ -d /opt/kde3/share/apps/konqueror/servicemenus ]; then
	for i in allfiles.desktop; do
		[ -L /opt/kde3/share/apps/konqueror/servicemenus/$i ] && \
			rm /opt/kde3/share/apps/konqueror/servicemenus/$i
	done
fi
if [ -d /opt/gnome/share/application-registry ]; then
	for i in jsummer.applications; do
		[ -L /opt/gnome/share/application-registry/$i ] && \
			rm /opt/gnome/share/application-registry/$i
	done
fi
if [ -d /opt/gnome/share/mime-info ]; then
	for i in jsummer.mime jsummer.keys; do
		[ -L /opt/gnome/share/mime-info/$i ] || \
			rm /opt/gnome/share/mime-info/$i
	done
fi


%files
%defattr(-,root,root)
%doc JSummer-%{version}/License.html JSummer-%{version}/Changelog
%{binprefix}/bin/jsummer
%{binprefix}/bin/jsummerx
%{binprefix}/bin/JSummer
%{binprefix}/bin/JSummerX
%{prefix}/share/man/man1/jsummer.1.gz
%{prefix}/share/pixmaps/JSummer.xpm
%{prefix}/share/applications/jsummer.desktop
%{prefix}/share/applications/JSummerX.desktop
%{prefix}/share/mime-info/jsummer.mime
%{prefix}/share/mime-info/jsummer.keys
%{prefix}/share/mime/packages/jsummer.xml
%{prefix}/share/mimelnk/application/digest-*.desktop
%{prefix}/share/apps/konqueror/servicemenus/allfiles.desktop
%{prefix}/share/application-registry/jsummer.applications
